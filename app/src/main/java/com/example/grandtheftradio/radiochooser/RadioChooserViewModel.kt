package com.example.grandtheftradio.radiochooser

import android.annotation.SuppressLint
import android.app.Application
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.icu.util.TimeZone
import android.os.Build
import android.util.ArrayMap
import android.util.ArraySet
import android.util.Log
import android.util.SparseArray
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.example.grandtheftradio.GrandTheftRadioApplication
import com.example.grandtheftradio.db.game.GameLocalDataSource
import com.example.grandtheftradio.db.radio.RadioLocalDataSource
import com.example.grandtheftradio.entities.Game
import com.example.grandtheftradio.entities.Radio
import com.example.grandtheftradio.entities.Tag
import com.example.grandtheftradio.network.FirebaseRemoteGameDataSource
import com.example.grandtheftradio.network.FirebaseRemoteRadioDataSource
import com.example.grandtheftradio.radioplayer.ExoPlayerManager
import com.example.grandtheftradio.repo.RadioRepository
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber


@FlowPreview
@ExperimentalCoroutinesApi
class RadioChooserViewModel(application: Application) : AndroidViewModel(application) {

    private var _games = MutableStateFlow<List<Game>>(mutableListOf())

    val db = (application as GrandTheftRadioApplication).db

    private val radioRepository = RadioRepository(
        FirebaseRemoteRadioDataSource(),
        RadioLocalDataSource(db.radioDao(), db.gameDao(), db.tagDao())
    )

    private var _currentGame: MutableLiveData<Game> = MutableLiveData()
    val currentGame: LiveData<Game>
        get() = _currentGame

    private var _radios = MutableLiveData<SnapshotStateList<Radio>>(SnapshotStateList())

    private var _tags = MutableLiveData<MutableList<Tag>>()
    val tags: LiveData<MutableList<Tag>>
        get() = _tags

    private var _selectedTags = MutableStateFlow<SnapshotStateList<Tag>>(SnapshotStateList())
    val selectedTags: StateFlow<SnapshotStateList<Tag>>
        get() = _selectedTags

    private var _currentRadio = MutableLiveData<Radio>()
    val currentRadio: LiveData<Radio>
        get() = _currentRadio

    private val _player = ExoPlayerManager.getSharedInstance(getApplication())

    private var isPlaying = false

    private var _isPlayerLoading = MutableStateFlow(false)
    val isPlayerLoading: StateFlow<Boolean>
        get() = _isPlayerLoading

    var _isPlayerHidden = MutableLiveData(true)
    val isPlayerHidden: LiveData<Boolean>
        get() = _isPlayerHidden

    private var _isLoading = MutableStateFlow(false)

    private var _currentPlaybackState = MutableLiveData(PlaybackState.ERROR)
    val currentPlaybackState: LiveData<PlaybackState>
        get() = _currentPlaybackState

    private var _radioByGame = MutableLiveData<ArrayMap<Game, SnapshotStateList<Radio>>>(ArrayMap())
    val radioByGame: LiveData<ArrayMap<Game, SnapshotStateList<Radio>>>
        get() = _radioByGame

    init {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            initializeGames()
        }
    }

    fun searchQuery(isFavorite: Boolean = false, query: String): List<Radio> {
        val searchSet = ArraySet<Radio>()
        if (query.isNotEmpty()) {
            val searchList = mutableListOf<Radio>()
            if (isFavorite) {
                _radios.value?.forEach {
                    if (it.favorite) {
                        searchList.add(it)
                    }
                }
            } else {
                searchList.addAll(_radios.value ?: listOf())
            }
            searchList.forEach {
                if (it.name.lowercase().contains(query.lowercase())) {
                    searchSet.add(it)
                }
                if (it.game.gameName.lowercase().contains(query.lowercase())) {
                    searchSet.add(it)
                }
            }
        } else {
            if (isFavorite) {
                val favoriteList = mutableListOf<Radio>()
                _radios.value?.forEach {
                    if (it.favorite) {
                        favoriteList.add(it)
                    }
                }
                searchSet.addAll(favoriteList)
            } else {
                searchSet.addAll(_radios.value ?: listOf())
            }
        }
        val filteredSet = ArraySet(searchSet)
        if (selectedTags.value.count() > 0) {
            searchSet.forEach { radio ->
                var tagCount = 0
                _selectedTags.value.forEach { tag ->
                    if (radio.tags.contains(tag)) {
                        tagCount = tagCount.inc()
                    }
                }
                if (tagCount == 0) {
                    filteredSet.remove(radio)
                }
            }
        }
        return filteredSet.toList()
    }

    fun updateRadio(radio: Radio) {
        viewModelScope.launch(Dispatchers.IO) {
            radioRepository.updateRadio(radio)
        }
        _radios.value = _radios.value
    }

    fun addSelectedTag(tag: Tag) {
        _selectedTags.value = _selectedTags.value.apply {
            this.add(tag)
        }
    }

    fun removeSelectedTag(tag: Tag) {
        _selectedTags.value = _selectedTags.value.apply {
            if (contains(tag)) {
                remove(tag)
            }
        }
    }

    @FlowPreview
    private suspend fun initializeGames() {
        radioRepository.getRadiosByGame(getApplication()).collect { map ->
            _radioByGame.postValue(map)
            val allRadios = SnapshotStateList<Radio>()
            map.keys.forEach {
                allRadios.addAll(map[it] ?: mutableListOf())
            }
            val allTags = ArraySet<Tag>()
            allRadios.forEach {
                allTags.addAll(it.tags)
            }
            _radios.postValue(allRadios)
            _tags.postValue(allTags.toMutableList())
            _games.value = map.keys.toList()
            _isLoading.value = false
        }
    }

    fun stopPlaying(isPlayerHidden: Boolean = false) {
        //stops media player
        if (isPlaying) {
            _player.stopPlayer(true)
            _currentPlaybackState.value = PlaybackState.PAUSED
            isPlaying = false
        }
        _isPlayerHidden.value = isPlayerHidden
    }

    fun playRadio(radio: Radio) {
        _currentRadio.value = radio
        _currentGame.value = radio.game
        isPlaying = false
        playStream(radio.link)
        _isPlayerHidden.value = false
    }

    fun playCurrent() {
        //start media player
        if (isPlaying) {
            _player.stopPlayer(true)
            _currentPlaybackState.value = PlaybackState.PAUSED
            isPlaying = false
        } else {
            playStream(_currentRadio.value!!.link)
        }
        _isPlayerHidden.value = false
    }

    private fun playStream(link: String) {
        playAudio(link)
        @SuppressLint("StaticFieldLeak") val mExtractor: YouTubeExtractor =
            object : YouTubeExtractor(getApplication()) {
                override fun onExtractionComplete(
                    ytFiles: SparseArray<YtFile>?,
                    videoMeta: VideoMeta?
                ) {
                    if (ytFiles != null) {
                        val itag = 251
                        val downloadUrl = ytFiles[itag].url
                        playAudio(downloadUrl)
                    }
                }
            }
        mExtractor.extract(link, true, true)
    }

    private fun playAudio(downloadUrl: String) {

        val p = _player.playerView.player
        _isPlayerLoading.value = true
        _currentPlaybackState.value = PlaybackState.LOADING
        _player.playStream(downloadUrl)

        p.addListener(object : Player.EventListener {
            override fun onTimelineChanged(
                timeline: Timeline,
                manifest: Any?,
                reason: Int
            ) {

            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
            ) {
            }

            override fun onLoadingChanged(isLoading: Boolean) {}
            override fun onPlayerStateChanged(
                playWhenReady: Boolean,
                playbackState: Int
            ) {
                if (playbackState == Player.STATE_READY && !isPlaying) {
                    val realDurationMillis = p.duration
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val ukrainianTime: Calendar =
                            GregorianCalendar(
                                TimeZone.getTimeZone("Europe/Kiev")
                            )
                        ukrainianTime.timeInMillis = Calendar.getInstance().timeInMillis
                        Timber.d(
                            ukrainianTime[Calendar.HOUR]
                                .toString() + " " + ukrainianTime[Calendar.MINUTE]
                        )
                        val startTime = 1610539200000L
                        val localTime = ukrainianTime.timeInMillis
                        val timePassed = localTime - startTime
                        val timesRepeat =
                            timePassed.toDouble() / realDurationMillis
                        Timber.d(
                            java.lang.Double.toString(timesRepeat % 1)
                        )
                        val trackSeek =
                            (timesRepeat % 1 * realDurationMillis).toLong()
                        p.seekTo(trackSeek)
                        _currentPlaybackState.value = PlaybackState.PLAYING
                        _isPlayerLoading.value = false
                        isPlaying = true
                    }
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {}
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
            override fun onPlayerError(error: ExoPlaybackException) {
                _currentPlaybackState.value = PlaybackState.ERROR
            }

            override fun onPositionDiscontinuity(reason: Int) {}
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
            override fun onSeekProcessed() {}
        })
    }
}
package com.example.coinmvvm.ui.fragment.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinmvvm.App.Companion.getString
import com.example.coinmvvm.R
import com.example.coinmvvm.constant.enums.SortState
import com.example.coinmvvm.data.model.RequestTickerData
import com.example.coinmvvm.data.model.Ticker
import com.example.coinmvvm.data.model.TickerData
import com.example.coinmvvm.data.repository.CoinRepository
import com.example.coinmvvm.extension.asLiveData
import com.example.coinmvvm.extension.asSingleLiveData
import com.example.coinmvvm.util.Resource
import com.example.coinmvvm.util.livedata.MutableSingleLiveData
import com.example.coinmvvm.util.livedata.NotNullMutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val _coinRepository: CoinRepository
) : ViewModel() {
    // 임시 데이터들
    private val _tmpTickerList: MutableList<Ticker> = mutableListOf()

    // 실제 데이터들
    private val _tickerList: MutableLiveData<List<Ticker>?> = MutableLiveData()
    val tickerList = _tickerList.asLiveData()

    // 코인리스트 정렬 State
    private val _sortEvent: NotNullMutableLiveData<SortState> = NotNullMutableLiveData(SortState.NO)
    val sortEvent = _sortEvent.asLiveData()

    // 검색 키워드
    private var _filterTickerSymbol: String = ""

    // 웹소켓 에러 핸들링 메시지
    private val _socketError: MutableSingleLiveData<String> = MutableSingleLiveData()
    val socketError = _socketError.asSingleLiveData()

    // 웹소켓 연결 여부
    private var _isSocketClose = true

    // 검색 키워드 TextChange Stream
    val filterTickerSymbol = { text: String ->
        _filterTickerSymbol = text
        notifySortedTickerList()
    }

    // 정렬 Change Stream
    val onTickerSortClick = { sortState: SortState -> onSortStateChanged(sortState) }

    init {
        viewModelScope.launch {
            // 웹소켓 Observable 구독
            _coinRepository.observeTickerSocket().collect { response ->
                when (response) {
                    is Resource.Success ->
                        onReceivedTicker(response.data)

                    is Resource.Error -> {
                        // 웹소켓 연결상태 설정
                        _isSocketClose = true
                        onError(response.message ?: getString(R.string.error_getting_coin_list))
                    }
                    // is Resource.Loading
                    else -> {}
                }
            }
        }
    }

    private suspend fun getKRWTickers(): Boolean {
        return when (val tickerList = _coinRepository.getKRWTickers()) {
            is Resource.Success -> {
                _tmpTickerList.clear()
                _tmpTickerList.addAll(tickerList.data)
                sortTicker()
                notifySortedTickerList()
                true
            }
            else -> false
        }
    }

    // 네트워크 연결 시 호출 or 홈 Fragment onResume 시 호출
    fun observeTickerPrice() {
        // 소켓이 연결되어 있는 상태라면 return
        if (!_isSocketClose) return

        _isSocketClose = false
        viewModelScope.launch {
            if (!getKRWTickers()) {
                _isSocketClose = true
                onError(getString(R.string.error_getting_coin_list))
                return@launch
            }

            when (_coinRepository.initTickerSocket()) {
                is Resource.Success -> {
                    // 재요청할 Ticker 목록
                    val requestTickerData = RequestTickerData(_tmpTickerList.map { it.getSymbolName() })
                    _coinRepository.requestTickerPrice(requestTickerData)
                }
                is Resource.Error -> {
                    _isSocketClose = true
                    onError(getString(R.string.error_getting_coin_list))
                }
                else -> {}
            }
        }
    }

    // 웹소켓 중단 - 홈 Fragment onStop 시 호출
    fun stopTickerSocket() {
        viewModelScope.launch {
            _coinRepository.stopTickerSocket()
        }
    }

    fun retryListenPrice() {
        stopTickerSocket()
        observeTickerPrice()
    }

    // 즐겨찾기 등록
    fun addFavoriteSymbol(symbol: String) {
        viewModelScope.launch {
            val index = _coinRepository.addFavoriteTicker(symbol)
            _tmpTickerList
                .find { it.getSymbolName() == symbol }
                ?.apply {
                    isFavorite = true
                    favoriteIndex = index
                }
            notifySortedTickerList()
        }
    }

    // 즐겨찾기 해제
    fun deleteFavoriteSymbol(symbol: String) {
        viewModelScope.launch {
            _coinRepository.deleteFavoriteTicker(symbol)
            _tmpTickerList
                .find { it.getSymbolName() == symbol }
                ?.apply { isFavorite = false }
            notifySortedTickerList()
        }
    }

    private fun onSortStateChanged(sortState: SortState) {
        if (_sortEvent.value == sortState) return

        _sortEvent.value = sortState
        _tickerList.value = null
        sortTicker()
        notifySortedTickerList()
    }

    // 코인리스트 Notify - 조건 정렬 포함
    // 즐겨찾기 변동
    // 검색어 변동
    // 네트워크 연결 및 홈 onResume
    private fun notifySortedTickerList() {
        filteringTicker()
    }

    private fun sortTicker() {
        Log.i("JAEJONG", "currentThread [${Thread.currentThread()}]")
        _tmpTickerList.apply {
            when (_sortEvent.value) {
                SortState.NO -> sortBy { it.index }

                SortState.NAME_DESC -> sortByDescending { it.symbol }
                SortState.NAME_ASC -> sortBy { it.symbol }

                SortState.PRICE_DESC -> sortByDescending { it.currentPrice.toFloat() }
                SortState.PRICE_ASC -> sortBy { it.currentPrice.toFloat() }

                SortState.RATE_DESC -> sortByDescending { it.getRateOfChange() }
                SortState.RATE_ASC -> sortBy { it.getRateOfChange() }

                SortState.VOLUME_DESC -> sortByDescending { it.transactionAmount.toFloat() }
                SortState.VOLUME_ASC -> sortBy { it.transactionAmount.toFloat() }
            }
        }
    }

    private fun filteringTicker() {
//        sortTicker()
        _tickerList.value = _tmpTickerList.let { tmpTikcers ->
            if (_filterTickerSymbol.isNotEmpty())
                tmpTikcers.filter { it.symbol.contains(_filterTickerSymbol, ignoreCase = true) }
            else
                tmpTikcers
        }
    }

    private fun onReceivedTicker(tickerData: TickerData) {
        val tickerContent = tickerData.ticker?.content ?: return

        _tmpTickerList.find { it.getSymbolName() == tickerContent.symbol }?.apply {
            currentPrice = tickerContent.closePrice
            prevPrice = tickerContent.prevClosePrice

            notifySortedTickerList()
        }
    }

    private fun onError(message: String) {
        _socketError.setValue(message)
    }

    override fun onCleared() {
        super.onCleared()
        stopTickerSocket()
    }
}
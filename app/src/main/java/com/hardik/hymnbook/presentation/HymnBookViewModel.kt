package com.hardik.hymnbook.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hardik.hymnbook.common.Resource
import com.hardik.hymnbook.data.dto.HymnBookIndexListItem
import com.hardik.hymnbook.data.dto.toBookItem
import com.hardik.hymnbook.domain.model.BookItem
import com.hardik.hymnbook.domain.repostitroy.HymnBookIndexRepository
import com.hardik.hymnbook.domain.repostitroy.HymnBookItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class HymnBookViewModel @Inject constructor(
    private val hymnBookIndexRepository: HymnBookIndexRepository,
    private val hymnBookItemRepository: HymnBookItemRepository
) : ViewModel() {
    private val TAG = HymnBookViewModel::class.java.simpleName

    private val _hymnbookIndex =
        MutableStateFlow<Resource<List<HymnBookIndexListItem>>>(Resource.Loading)
    //val hymnbookIndex: StateFlow<Resource<List<HymnBookIndexListItem>>> = _hymnbookIndex //or
    val hymnbookIndex = _hymnbookIndex.asStateFlow()

    protected val _bookItem = MutableStateFlow<Resource<BookItem>>(Resource.Loading)

    init {
        getHymnBookIndexList()
    }

    private fun getHymnBookIndexList() = viewModelScope.launch {
        _hymnbookIndex.value = Resource.Loading

        hymnBookIndexRepository.getHymnBookIndexList().let { resource ->
            Log.d(TAG, "getHymnBookIndexList: ")
            when (resource) {
                is Resource.Success -> {
                    delay(1000)
                    // Update the list with the received data and notify observers
                    _hymnbookIndex.value = resource
                }

                is Resource.Error -> {
                    // Handle error
                    _hymnbookIndex.value = Resource.Error("Unknown error")
                }

                is Resource.Loading -> {
                    // Show loading state
                    _hymnbookIndex.value = Resource.Loading
                }
            }
        }
    }

    protected open fun getBookItems(fileName: String) = viewModelScope.launch {
        _bookItem.value = Resource.Loading

        hymnBookItemRepository.getHymnBookItem(fileName = fileName).let { resource ->
            Log.d(TAG, "getBookItems: $fileName")
            when (resource) {
                is Resource.Success -> {
                    delay(1000)
                    // Update the list with the received data and notify observers
                    _bookItem.value = Resource.Success(data = resource.data.toBookItem())
                }

                is Resource.Error -> {
                    // Handle error
                    _bookItem.value = Resource.Error("Unknown error")
                }

                is Resource.Loading -> {
                    // Show loading state
                    _bookItem.value = Resource.Loading
                }
            }
        }

    }
    
}




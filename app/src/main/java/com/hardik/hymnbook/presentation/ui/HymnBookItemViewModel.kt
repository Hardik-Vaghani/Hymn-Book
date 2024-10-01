package com.hardik.hymnbook.presentation.ui

import com.hardik.hymnbook.domain.repostitroy.HymnBookIndexRepository
import com.hardik.hymnbook.domain.repostitroy.HymnBookItemRepository
import com.hardik.hymnbook.presentation.HymnBookViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HymnBookItemViewModel @Inject constructor(private val indexRepository: HymnBookIndexRepository,
                                                private val itemRepository: HymnBookItemRepository
): HymnBookViewModel(hymnBookIndexRepository=indexRepository, hymnBookItemRepository = itemRepository){
    private val TAG = HymnBookItemViewModel::class.java.simpleName

    // get this data from parent viewmodel '_bookItem'
    val bookItem = _bookItem.asStateFlow()

    public override fun getBookItems(fileName: String) =
        super.getBookItems(fileName = fileName)
}
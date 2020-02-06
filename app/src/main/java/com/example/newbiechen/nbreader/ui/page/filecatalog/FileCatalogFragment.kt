package com.example.newbiechen.nbreader.ui.page.filecatalog

import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.data.entity.LocalBookEntity
import com.example.newbiechen.nbreader.databinding.FragmentFileCatalogBinding
import com.example.newbiechen.nbreader.ui.page.base.BaseBindingFragment
import com.example.newbiechen.nbreader.ui.page.filesystem.ILocalBookSystem
import com.example.newbiechen.nbreader.ui.page.filesystem.ILocalBookCallback

/**
 *  author : newbiechen
 *  date : 2019-08-17 14:49
 *  description :
 */

class FileCatalogFragment : BaseBindingFragment<FragmentFileCatalogBinding>(), ILocalBookSystem {

    companion object {
        private const val TAG = "FileCatalogFragment"
        fun newInstance() = FileCatalogFragment()
    }

    override fun initContentView(): Int = R.layout.fragment_file_catalog

    override fun setBookCheckedAll(isChecked: Boolean) {
    }

    override fun getCheckedBookCount(): Int {
        return 0
    }

    override fun deleteCheckedBooks() {
    }

    override fun getCheckedBooks(): List<LocalBookEntity> {
        return ArrayList()
    }

    override fun setBookCallback(callback: ILocalBookCallback) {
    }

    override fun getBookCount(): Int {
        return 0
    }

    override fun saveCheckedBooks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
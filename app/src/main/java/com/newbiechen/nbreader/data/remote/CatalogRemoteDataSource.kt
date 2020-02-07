package com.newbiechen.nbreader.data.remote

import com.newbiechen.nbreader.data.entity.CatalogEntity
import com.newbiechen.nbreader.data.entity.CatalogLabelEntity
import com.newbiechen.nbreader.data.remote.api.BookApi
import com.newbiechen.nbreader.data.repository.impl.ICatalogRepository
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRemoteDataSource @Inject constructor(private val bookApi: BookApi) : ICatalogRepository {

    override fun getCatalogItems(): Flowable<List<CatalogEntity>> {
        val catalogFlowable = bookApi.getCatalog()
        val catalogLabelFlowable = bookApi.getCatalogLabel()

        return Flowable.zip(catalogFlowable, catalogLabelFlowable, BiFunction { t1, t2 ->
            // 创建一个新的 List
            val catalogEntities = ArrayList<CatalogEntity>(t1.male.size + t2.female.size)
            catalogEntities.addAll(combineCatalogAndLabels(t1.male, t2.male))
            catalogEntities.addAll(combineCatalogAndLabels(t1.female, t2.female))
            catalogEntities
        })
    }

    private fun combineCatalogAndLabels(
        catalogs: List<CatalogEntity>,
        catalogLabels: List<CatalogLabelEntity>
    ): List<CatalogEntity> {
        val catalogEntities = ArrayList<CatalogEntity>(catalogs.size)
        // 遍历数组
        catalogs.forEach { catalogEntity ->
            // 查找与 catalogEntity 匹配的 catalogLabel
            val catalogLabel = catalogLabels.first { catalogLabelEntity ->
                catalogEntity.name == catalogLabelEntity.major
            }

            // 将 catalog 和 catalogLabel 合并生成新的 catalog，并添加到列表中
            catalogEntities.add(catalogEntity.run {
                CatalogEntity(alias, name, bookCover, catalogLabel.mins)
            })
        }
        return catalogEntities
    }

    override fun saveCatalogItems(entities: List<CatalogEntity>) {
    }
}
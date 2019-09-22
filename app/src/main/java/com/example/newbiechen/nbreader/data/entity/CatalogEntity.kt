package com.example.newbiechen.nbreader.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.*
import com.example.newbiechen.nbreader.data.local.room.converter.StringListConverter
import com.google.gson.annotations.SerializedName

data class CatalogWrapper(
    // 女生分类
    @SerializedName("female")
    val female: List<CatalogEntity>,
    // 男生分类
    @SerializedName("male")
    val male: List<CatalogEntity>,
    val ok: Boolean
)

@Entity(tableName = "catalog_entity")
@TypeConverters(StringListConverter::class)
data class CatalogEntity(
    @NonNull
    @PrimaryKey
    @SerializedName("alias")
    @ColumnInfo(name = "alias")
    val alias: String,
    @SerializedName("title")
    @ColumnInfo(name = "title")
    val name: String,
    @SerializedName("bookCover")
    @ColumnInfo(name = "bookCover")
    val bookCover: List<String>,
    @ColumnInfo(name = "labels")
    val labels: List<String> // 子标签数据，不是 json 解析的一部分。
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(alias)
        parcel.writeString(name)
        parcel.writeStringList(bookCover)
        parcel.writeStringList(labels)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CatalogEntity> {
        override fun createFromParcel(parcel: Parcel): CatalogEntity {
            return CatalogEntity(parcel)
        }

        override fun newArray(size: Int): Array<CatalogEntity?> {
            return arrayOfNulls(size)
        }
    }
}
package moe.youranime.main.ui.home

import moe.youranime.main.config.Configuration
import type.ShowType

class Show(
    val bannerUrl: String,
    val description: String,
    val descriptionRecord: TranslatableRecord,
    val dubbed: Boolean,
    val subbed: Boolean,
    val id: String,
    val published: Boolean,
    val seasonsCount: Int,
    val showType: ShowType,
    val title: String,
    val titleRecord: TranslatableRecord
) {
    override fun toString(): String {
        return String.format("{id: \"%s\" title: \"%s\"}", title, id)
    }
    class Builder() {
        private lateinit var bannerUrl: String
        private lateinit var description: String
        private lateinit var descriptionRecord: TranslatableRecord
        private var dubbed = false
        private var subbed = false
        private lateinit var id: String
        private var published = false
        private var seasonsCount = 0
        private lateinit var showType: ShowType
        private lateinit var title: String
        private lateinit var titleRecord: TranslatableRecord

        fun bannerUrl(bannerUrl: String?) = apply { this.bannerUrl = bannerUrl ?: Configuration.bannerNotFoundUrl }
        fun description(description: String) = apply { this.description = description }
        fun descriptionRecord(en: String, fr: String?, jp: String?) = apply { this.descriptionRecord = TranslatableRecord(en, fr, jp) }
        fun dubbed(dubbed: Boolean) = apply { this.dubbed = dubbed }
        fun subbed(subbed: Boolean) = apply { this.subbed = subbed }
        fun id(id: String) = apply { this.id = id }
        fun published(published: Boolean) = apply { this.published = published }
        fun seasonsCount(seasonsCount: Int) = apply { this.seasonsCount = seasonsCount }
        fun showType(showType: ShowType) = apply { this.showType = showType }
        fun title(title: String) = apply { this.title = title }
        fun titleRecord(en: String, fr: String?, jp: String?) = apply { this.titleRecord = TranslatableRecord(en, fr, jp) }
        fun build(): Show {
            return Show(
                bannerUrl = bannerUrl,
                description = description,
                descriptionRecord = descriptionRecord,
                dubbed = dubbed,
                subbed = subbed,
                id = id,
                published = published,
                seasonsCount = seasonsCount,
                showType = showType,
                title = title,
                titleRecord = titleRecord
            )
        }
    }

    companion object {
        fun create(fetchedShowData: AllShowsQuery.AllShow?): Show? {
            if (fetchedShowData == null) { return null }

            return Builder()
                .id(fetchedShowData.id)
                .bannerUrl(fetchedShowData.bannerUrl)
                .description(fetchedShowData.description)
                .descriptionRecord(
                    fetchedShowData.descriptionRecord.en,
                    fetchedShowData.descriptionRecord.fr,
                    fetchedShowData.descriptionRecord.jp
                )
                .dubbed(fetchedShowData.dubbed)
                .subbed(fetchedShowData.subbed)
                .published(fetchedShowData.published)
                .seasonsCount(fetchedShowData.seasonsCount)
                .showType(fetchedShowData.showType)
                .title(fetchedShowData.title)
                .titleRecord(
                    fetchedShowData.titleRecord.en,
                    fetchedShowData.titleRecord.fr,
                    fetchedShowData.titleRecord.jp
                )
                .build()
        }
    }
}
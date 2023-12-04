package kr.galaxyhub.sc.news.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import java.time.LocalDateTime
import java.util.EnumSet
import kr.galaxyhub.sc.common.domain.PrimaryKeyEntity

@Entity
class News(
    newsType: NewsType,
    originId: Long,
    publishedAt: LocalDateTime,
) : PrimaryKeyEntity() {

    @Enumerated(EnumType.STRING)
    @Column(name = "news_type", nullable = false)
    var newsType: NewsType = newsType
        private set

    @Column(name = "origin_id", nullable = false)
    var originId: Long = originId
        private set

    @Column(name = "published_at", nullable = false)
    var publishedAt: LocalDateTime = publishedAt
        private set

    @Embedded
    var newsInformation: NewsInformation = NewsInformation.EMPTY
        private set

    @Convert(converter = EnumSetLanguageConverter::class)
    @Column(name = "support_languages", nullable = false)
    private val mutableSupportLanguages: EnumSet<Language> = EnumSet.noneOf(Language::class.java)
    val supportLanguages: Set<Language> get() = mutableSupportLanguages.toHashSet()

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST], mappedBy = "news")
    private val contents: MutableList<Content> = mutableListOf()

    fun addContent(content: Content) {
        validateAddContent(content)
        if (mutableSupportLanguages.isEmpty()) {
            newsInformation = content.newsInformation
        }
        contents.add(content)
        mutableSupportLanguages.add(content.language)
    }

    private fun validateAddContent(content: Content) {
        if (content.news == null) {
            throw IllegalArgumentException("추가할 컨텐츠에 뉴스가 등록되어 있지 않습니다.") // TODO 명확한 예외 정의할 것
        }
        if (content.news != this) {
            throw IllegalArgumentException("컨텐츠에 등록된 뉴스가 동일하지 않습니다.") // TODO 명확한 예외 정의할 것
        }
        if (mutableSupportLanguages.contains(content.language)) {
            throw IllegalArgumentException("이미 해당 언어로 작성된 뉴스가 있습니다.") // TODO 명확한 예외 정의할 것
        }
    }
}

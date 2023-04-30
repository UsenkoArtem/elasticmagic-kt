package dev.evo.elasticmagic.qf

import dev.evo.elasticmagic.ElasticsearchTestBase
import dev.evo.elasticmagic.SearchQuery
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AttrSimpleFiltersTest : ElasticsearchTestBase() {
    override val indexName = "attr-simple-filter"

    object ItemQueryFilters : QueryFilters() {
        val selectAttrs by AttrSimpleFilter(ItemDoc.selectAttrs, "attr")
        val rangeAttrs by AttrRangeSimpleFilter(ItemDoc.rangeAttrs, "attr")
        val boolAttrs by AttrBoolSimpleFilter(ItemDoc.boolAttrs, "attr")
    }

    @Test
    fun attrSimpleFilterTest() = runTestWithSerdes {
        withFixtures(ItemDoc, FIXTURES) {
            val searchQuery = SearchQuery()
            searchQuery.execute(index).totalHits shouldBe 8

            ItemQueryFilters.apply(
                searchQuery,
                mapOf(listOf("attr", Manufacturer.ATTR_ID.toString(), "any") to listOf("1", "0"))
            ).let {
                val searchResult = searchQuery.execute(index)
                searchResult.totalHits shouldBe 4
            }
        }
    }

    @Test
    fun attrBoolSimpleFilterTest() = runTestWithSerdes {
        withFixtures(ItemDoc, FIXTURES) {
            val searchQuery = SearchQuery()
            searchQuery.execute(index).totalHits shouldBe 8

            ItemQueryFilters.apply(
                searchQuery,
                mapOf(listOf("attr", ExtensionSlot.ATTR_ID.toString()) to listOf("true"))
            ).let {
                val searchResult = searchQuery.execute(index)
                searchResult.totalHits shouldBe 3
            }
        }
    }

    @Test
    fun attrRangeSimpleFilterTest() = runTestWithSerdes {
        withFixtures(ItemDoc, FIXTURES) {
            val searchQuery = SearchQuery()
            searchQuery.execute(index).totalHits shouldBe 8

            ItemQueryFilters.apply(
                searchQuery,
                mapOf(listOf("attr", DisplaySize.ATTR_ID.toString(), "gte") to listOf("6.7"))
            ).let {
                val searchResult = searchQuery.execute(index)
                searchResult.totalHits shouldBe 3
            }
        }
    }

    @Test
    fun applyAllSimpleFilters() = runTestWithSerdes {
        withFixtures(ItemDoc, FIXTURES) {
            val searchQuery = SearchQuery()
            searchQuery.execute(index).totalHits shouldBe 8

            ItemQueryFilters.apply(
                searchQuery,
                mapOf(
                    listOf("attr", Manufacturer.ATTR_ID.toString(), "any") to listOf("1", "0"),
                    listOf("attr", ExtensionSlot.ATTR_ID.toString()) to listOf("false"),
                    listOf("attr", DisplaySize.ATTR_ID.toString(), "gte") to listOf("6.3"),
                )
            ).let {
                val searchResult = searchQuery.execute(index)
                searchResult.totalHits shouldBe 2
            }
        }
    }
}

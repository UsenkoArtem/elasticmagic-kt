package dev.evo.elasticmagic.serde.json.compile

import dev.evo.elasticmagic.Document
import dev.evo.elasticmagic.ElasticsearchVersion
import dev.evo.elasticmagic.SearchQuery
import dev.evo.elasticmagic.compile.SearchQueryCompiler
import dev.evo.elasticmagic.serde.json.JsonSerializer

import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

// import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlinx.serialization.serializer
import kotlin.reflect.typeOf

import kotlin.test.Test

class SearchQueryCompilerJsonTests {
    private val compiler = SearchQueryCompiler(
        ElasticsearchVersion(6, 0, 0),
    )

    @Test
    fun testEmpty() {
        val compiled = compiler.compile(JsonSerializer, SearchQuery())
        compiled.body shouldBe buildJsonObject {}
    }

    @Test
    fun testComposeFilters() {
        val userDoc = object : Document() {
            val status by int()
            val rank by float()
            val opinionsCount by int("opinions_count")
        }

        val query = SearchQuery()
            .filter(userDoc.status.eq(0))
            .filter(userDoc.rank.gte(90.0))
            .filter(userDoc.opinionsCount.gt(5))

        val res = compiler.compile(JsonSerializer, query)
        res.body shouldBe buildJsonObject {
            putJsonObject("query") {
                putJsonObject("bool") {
                    putJsonArray("filter") {
                        addJsonObject {
                            putJsonObject("term") {
                                put("status", 0)
                            }
                        }
                        addJsonObject {
                            putJsonObject("range") {
                                putJsonObject("rank") {
                                    put("gte", 90.0)
                                }
                            }
                        }
                        addJsonObject {
                            putJsonObject("range") {
                                putJsonObject("opinions_count") {
                                    put("gt", 5)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
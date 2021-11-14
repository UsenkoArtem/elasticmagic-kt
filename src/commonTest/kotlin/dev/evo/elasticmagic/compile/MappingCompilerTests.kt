package dev.evo.elasticmagic.compile

import dev.evo.elasticmagic.BaseTest
import dev.evo.elasticmagic.ElasticsearchVersion
import dev.evo.elasticmagic.Params
import dev.evo.elasticmagic.doc.BoundField
import dev.evo.elasticmagic.doc.DocSourceField
import dev.evo.elasticmagic.doc.Document
import dev.evo.elasticmagic.types.KeywordType
import dev.evo.elasticmagic.doc.RuntimeFields
import dev.evo.elasticmagic.doc.SubDocument
import dev.evo.elasticmagic.doc.SubFields
import dev.evo.elasticmagic.doc.datetime
import dev.evo.elasticmagic.doc.mergeDocuments
import dev.evo.elasticmagic.query.Script

import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.types.shouldBeInstanceOf

import kotlin.test.Test

class MappingCompilerTests : BaseTest() {
    val esVersion = ElasticsearchVersion(6, 0, 0)
    private val compiler = MappingCompiler(
        esVersion,
        SearchQueryCompiler(esVersion)
    )

    private fun compile(doc: Document): Map<String, Any?> {
        val compiled = compiler.compile(serializer, doc)
        return compiled.body.shouldBeInstanceOf<TestSerializer.ObjectCtx>().toMap()
    }

    @Test
    fun testEmptyMapping() {
        val emptyDoc = object : Document() {}

        compile(emptyDoc) shouldContainExactly mapOf(
            "properties" to emptyMap<String, Any>()
        )
    }

    @Test
    fun testSubFields() {
        class NameFields<V>(field: BoundField<V, V>) : SubFields<V>(field) {
            val sort by keyword(normalizer = "lowercase")
            val autocomplete by text(analyzer = "ngram")
        }

        val productDoc = object : Document() {
            val name by text(analyzer = "standard").subFields(::NameFields)
            val keywords by text().subFields(::NameFields)
        }

        compile(productDoc) shouldContainExactly mapOf(
            "properties" to mapOf(
                "name" to mapOf(
                    "type" to "text",
                    "analyzer" to "standard",
                    "fields" to mapOf(
                        "sort" to mapOf(
                            "type" to "keyword",
                            "normalizer" to "lowercase",
                        ),
                        "autocomplete" to mapOf(
                            "type" to "text",
                            "analyzer" to "ngram",
                        )
                    )
                ),
                "keywords" to mapOf(
                    "type" to "text",
                    "fields" to mapOf(
                        "sort" to mapOf(
                            "type" to "keyword",
                            "normalizer" to "lowercase",
                        ),
                        "autocomplete" to mapOf(
                            "type" to "text",
                            "analyzer" to "ngram",
                        )
                    )
                )
            )
        )
    }

    @Test
    fun testSubDocument() {
        class OpinionDoc(field: DocSourceField) : SubDocument(field) {
            val count by int()
        }

        class CompanyDoc(field: DocSourceField) : SubDocument(field) {
            val name by text(analyzer = "standard")
            val opinion by obj(::OpinionDoc, params = Params("enabled" to false))
        }

        val userDoc = object : Document() {
            val company by obj(::CompanyDoc)
            val opinion by obj(::OpinionDoc)
        }

        compile(userDoc) shouldContainExactly mapOf(
            "properties" to mapOf(
                "company" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "name" to mapOf(
                            "type" to "text",
                            "analyzer" to "standard"
                        ),
                        "opinion" to mapOf(
                            "type" to "object",
                            "enabled" to false,
                            "properties" to mapOf(
                                "count" to mapOf(
                                    "type" to "integer"
                                )
                            )
                        ),
                    )
                ),
                "opinion" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "count" to mapOf(
                            "type" to "integer"
                        )
                    )
                ),
            )
        )
    }

    @Test
    fun testRuntimeFields() {
        val logDoc = object : Document() {
            // TODO: Replace with date field
            val timestamp by datetime("@timestamp")

            override val runtime = object : RuntimeFields() {
                val dayOfWeek by runtime(
                    "day_of_week",
                    KeywordType,
                    Script.Source(
                        """
                        emit(
                            doc[params.timestampField].value
                                .dayOfWeekEnum
                                .getDisplayName(TextStyle.FULL, Locale.ROOT)
                        )""".trimIndent(),
                        params = Params(
                            "timestampField" to timestamp
                        )
                    )
                )
            }
        }

        compile(logDoc) shouldContainExactly mapOf(
            "runtime" to mapOf(
                "day_of_week" to mapOf(
                    "type" to "keyword",
                    "script" to mapOf(
                        "source" to """
                            emit(
                                doc[params.timestampField].value
                                    .dayOfWeekEnum
                                    .getDisplayName(TextStyle.FULL, Locale.ROOT)
                            )""".trimIndent(),
                        "params" to mapOf(
                            "timestampField" to "@timestamp"
                        )
                    )
                )
            ),
            "properties" to mapOf(
                "@timestamp" to mapOf(
                    "type" to "date",
                ),
            )
        )
    }

    @Test
    fun testMergeDocuments() {
        open class QADoc : Document() {
            val join by join(relations = mapOf("question" to listOf("answer")))
        }

        val questionDoc = object : QADoc() {
            val id by int()
            val text by text()
        }

        val answerDoc = object : QADoc() {
            override val meta = questionDoc.meta

            val id by int()
            val text by text()
            val accepted by boolean()
        }

        val commonDoc = mergeDocuments(questionDoc, answerDoc)

        compile(commonDoc) shouldContainExactly mapOf(
            "properties" to mapOf(
                "join" to mapOf(
                    "type" to "join",
                    "relations" to mapOf(
                        "question" to listOf("answer")
                    )
                ),
                "id" to mapOf(
                    "type" to "integer"
                ),
                "text" to mapOf(
                    "type" to "text"
                ),
                "accepted" to mapOf(
                    "type" to "boolean"
                )
            )
        )
    }
}

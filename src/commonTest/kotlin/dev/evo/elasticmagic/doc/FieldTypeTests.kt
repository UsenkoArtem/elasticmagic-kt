package dev.evo.elasticmagic.doc

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

import kotlin.test.Test

class FieldTypeTests {
    @Test
    fun intRangeType() {
        IntRangeType.serialize(Range(gte = 5, lte = 10))
            .shouldBeInstanceOf<Map<String, Any>>() shouldContainExactly mapOf(
                "gte" to 5,
                "lte" to 10,
            )
        IntRangeType.serialize(Range(gt = -30, lt = 0))
            .shouldBeInstanceOf<Map<String, Any>>() shouldContainExactly mapOf(
                "gt" to -30,
                "lt" to 0,
        )

        IntRangeType.deserialize(mapOf("gt" to 1)) shouldBe Range(gt = 1)
        IntRangeType.deserialize(mapOf("lte" to "-1")) shouldBe Range(lte = -1)
        shouldThrow<ValueDeserializationException> {
            IntRangeType.deserialize(mapOf("gt" to Int.MAX_VALUE.toLong() + 1))
        }
        shouldThrow<ValueDeserializationException> {
            IntRangeType.deserialize(mapOf("lt" to Int.MIN_VALUE.toLong() - 1))
        }
        shouldThrow<ValueDeserializationException> {
            IntRangeType.deserialize(mapOf("gte" to "one"))
        }

        IntRangeType.serializeTerm(0) shouldBe 0
        IntRangeType.serializeTerm(0) shouldBe 0

        IntRangeType.deserializeTerm(-1) shouldBe -1
        shouldThrow<ValueDeserializationException> {
            IntRangeType.deserializeTerm(-1.0)
        }
    }

    @Test
    fun longRangeType() {
        LongRangeType.serialize(Range(gte = 5, lte = 10))
            .shouldBeInstanceOf<Map<String, Any>>() shouldContainExactly mapOf(
                "gte" to 5L,
                "lte" to 10L,
            )
        LongRangeType.serialize(Range(gt = -30, lt = 0))
            .shouldBeInstanceOf<Map<String, Any>>() shouldContainExactly mapOf(
                "gt" to -30L,
                "lt" to 0L,
            )

        LongRangeType.deserialize(mapOf("gt" to 1)) shouldBe Range(gt = 1L)
        LongRangeType.deserialize(mapOf("lt" to Long.MIN_VALUE)) shouldBe Range(lt = Long.MIN_VALUE)
        LongRangeType.deserialize(mapOf("lte" to "-1")) shouldBe Range(lte = -1L)
        shouldThrow<ValueDeserializationException> {
            LongRangeType.deserialize(mapOf("gte" to 1.0))
        }
        shouldThrow<ValueDeserializationException> {
            LongRangeType.deserialize(mapOf("lte" to "max"))
        }

        LongRangeType.serializeTerm(0) shouldBe 0L
        LongRangeType.serializeTerm(1L) shouldBe 1L

        LongRangeType.deserializeTerm(-1) shouldBe -1
        LongRangeType.deserializeTerm("0") shouldBe 0L
        shouldThrow<ValueDeserializationException> {
            LongRangeType.deserializeTerm(0.0)
        }
        shouldThrow<ValueDeserializationException> {
            LongRangeType.deserializeTerm("0.0")
        }
    }

    @Test
    fun floatRangeType() {
        FloatRangeType.serialize(Range(gte = 5.0F, lte = 10.0F))
            .shouldBeInstanceOf<Map<String, Any>>() shouldContainExactly mapOf(
                "gte" to 5.0F,
                "lte" to 10.0F,
            )
        FloatRangeType.serialize(Range(gt = -30F, lt = 0F))
            .shouldBeInstanceOf<Map<String, Any>>() shouldContainExactly mapOf(
                "gt" to -30F,
                "lt" to 0F,
            )

        FloatRangeType.deserialize(mapOf("gt" to 1)) shouldBe Range(gt = 1F)
        FloatRangeType.deserialize(mapOf("lt" to Int.MAX_VALUE.toFloat())) shouldBe
                Range(lt = Int.MAX_VALUE.toFloat())
        FloatRangeType.deserialize(mapOf("lt" to Int.MAX_VALUE.toFloat())) shouldBe
                Range(lt = (Int.MAX_VALUE - 1).toFloat())
        FloatRangeType.deserialize(mapOf("lte" to "-1")) shouldBe Range(lte = -1F)
        FloatRangeType.deserialize(mapOf("gte" to "-1.1")) shouldBe Range(gte = -1.1F)
        shouldThrow<ValueDeserializationException> {
            FloatRangeType.deserialize(mapOf("gte" to "+Inf"))
        }

        FloatRangeType.serializeTerm(0F) shouldBe 0.0F

        FloatRangeType.deserializeTerm(-1) shouldBe -1F
        FloatRangeType.deserializeTerm("0.0") shouldBe 0F
        FloatRangeType.deserializeTerm("NaN") shouldBe Float.NaN
        shouldThrow<ValueDeserializationException> {
            FloatRangeType.deserializeTerm("-Inf")
        }
    }

    @Test
    fun doubleRangeType() {
        DoubleRangeType.serialize(Range(gt = 5.0, gte = 5.0, lt = 20.0, lte = 10.0))
            .shouldBeInstanceOf<Map<String, Any>>() shouldContainExactly mapOf(
                "gt" to 5.0,
                "gte" to 5.0,
                "lt" to 20.0,
                "lte" to 10.0,
            )

        DoubleRangeType.deserialize(mapOf("gt" to 1)) shouldBe Range(gt = 1.0)
        DoubleRangeType.deserialize(mapOf("lt" to Int.MAX_VALUE.toDouble())) shouldBe
                Range(lt = Int.MAX_VALUE.toDouble())
        DoubleRangeType.deserialize(mapOf("lt" to Int.MAX_VALUE.toDouble())) shouldNotBe
                Range(lt = (Int.MAX_VALUE - 1).toDouble())
        DoubleRangeType.deserialize(mapOf("lt" to Long.MAX_VALUE.toDouble())) shouldBe
                Range(lt = Long.MAX_VALUE.toDouble())
        DoubleRangeType.deserialize(mapOf("lt" to Long.MAX_VALUE.toDouble())) shouldBe
                Range(lt = (Long.MAX_VALUE - 1).toDouble())
        DoubleRangeType.deserialize(mapOf("lte" to "-1")) shouldBe Range(lte = -1.0)
        DoubleRangeType.deserialize(mapOf("gte" to "-1.1")) shouldBe Range(gte = -1.1)
        shouldThrow<ValueDeserializationException> {
            DoubleRangeType.deserialize(mapOf("gte" to "+Inf"))
        }

        DoubleRangeType.serializeTerm(0.1) shouldBe 0.1

        DoubleRangeType.deserializeTerm(-1) shouldBe -1.0
        DoubleRangeType.deserializeTerm("0.0") shouldBe 0.0
        DoubleRangeType.deserializeTerm(Long.MIN_VALUE) shouldBe Long.MIN_VALUE.toDouble()
        shouldThrow<ValueDeserializationException> {
            DoubleRangeType.deserializeTerm("-Inf")
        }
    }
}

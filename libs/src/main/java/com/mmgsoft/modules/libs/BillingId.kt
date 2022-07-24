package com.mmgsoft.modules.libs


@Suppress("MemberVisibilityCanBePrivate")
sealed class BillingId(
    val interstitial: String,
    val banner: String,
    val consume1: String,
    val consume2: String,
    val consume3: String,
) {
    protected abstract val prefixId: String

    open fun toList(): List<String> {
        return listOf(interstitial, banner, consume1, consume2, consume3)
    }

    override fun toString(): String {
        return buildString {
            appendLine("INTERSTITIAL: $interstitial")
            appendLine("BANNER: $banner")
            appendLine("CONSUME1: $consume1")
            appendLine("CONSUME2: $consume2")
            appendLine("CONSUME3: $consume3")
        }
    }

    class Google(
        override val prefixId: String,
        val noneConsume1: String = "$prefixId.subs.nonconsum.item1"
    ) : BillingId(
        "$prefixId.inapp.nonconsum.rminitial",
        "$prefixId.inapp.nonconsum.rmbanner",
        "$prefixId.inapp.consume.item1",
        "$prefixId.inapp.consume.item2",
        "$prefixId.inapp.consume.item3",
    ) {
        override fun toString(): String {
            return buildString {
                appendLine(super.toString())
                appendLine("NONE_CONSUME1: $noneConsume1")
            }
        }
    }

    class Amazon(
        override val prefixId: String,
        val entitleDiscount1: String = "$prefixId.amziap.entitle.buygold.discount1"
    ) : BillingId(
        "$prefixId.amziap.subs.rminitial",
        "$prefixId.amziap.subs.rmbanner",
        "$prefixId.amziap.consum.buygold1",
        "$prefixId.amziap.consum.buygold2",
        "$prefixId.amziap.consum.buygold3",
    ) {

        override fun toList(): List<String> {
            return super.toList().plus(entitleDiscount1)
        }

        override fun toString(): String {
            return buildString {
                appendLine(super.toString())
                appendLine("ENTITLE_DISCOUNT1: $entitleDiscount1")
            }
        }
    }
}
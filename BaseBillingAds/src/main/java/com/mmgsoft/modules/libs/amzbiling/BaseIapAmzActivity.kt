package com.mmgsoft.modules.libs.amzbiling

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amazon.device.iap.PurchasingListener
import com.amazon.device.iap.PurchasingService
import com.amazon.device.iap.model.*
import com.mmgsoft.modules.libs.AdsComponents
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.data.local.db.DbHelper
import com.mmgsoft.modules.libs.data.model.db.EntitlementModel
import com.mmgsoft.modules.libs.data.model.db.SubscriptionModel
import com.mmgsoft.modules.libs.etx.setStatusBarColor
import com.mmgsoft.modules.libs.etx.setStatusBarTextColorDark
import com.mmgsoft.modules.libs.helpers.AmazonScreenType
import com.mmgsoft.modules.libs.manager.MoneyManager
import com.mmgsoft.modules.libs.utils.DEFAULT_EXCHANGE_RATE_OTHER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseIapAmzActivity : AppCompatActivity(), PurchasingListener {
    private var allProductSkus: MutableSet<String> = HashSet()
    var currentUserId: String? = null
    var currentMarketPlace: String? = null
    abstract val allSkus: Set<String>
    abstract val resLayout: Int
    abstract fun initData()
    abstract fun notifyUpdateListView()
    protected abstract val screenType: AmazonScreenType
    protected val productItems = arrayListOf<ProductItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(R.color.white)
        setStatusBarTextColorDark()
        allProductSkus.clear()
        allProductSkus.addAll(allSkus)
        setContentView(resLayout)
        PurchasingService.registerListener(this, this)
        initData()
    }

    override fun onResume() {
        super.onResume()
        PurchasingService.getUserData()
        PurchasingService.getPurchaseUpdates(true)
        if (allProductSkus.isNotEmpty())
            PurchasingService.getProductData(allProductSkus.toSet())
    }

    override fun onUserDataResponse(userDataResponse: UserDataResponse) {
        userDataResponse.requestStatus?.let {
            when (it) {
                UserDataResponse.RequestStatus.SUCCESSFUL -> {
                    currentUserId = userDataResponse.userData.userId
                    currentMarketPlace = userDataResponse.userData.marketplace
                }
                UserDataResponse.RequestStatus.FAILED -> {}
                UserDataResponse.RequestStatus.NOT_SUPPORTED -> {}
            }
        }
    }

    override fun onProductDataResponse(response: ProductDataResponse) {
        when (response.requestStatus) {
            ProductDataResponse.RequestStatus.SUCCESSFUL -> {
                val products = response.productData
                val skuUnavailables = response.unavailableSkus
                productItems.clear()
                for (key in products.keys) {
                    products[key]?.let { product ->
                        val productItem = ProductItem()
                        productItem.title = product.title ?: ""
                        productItem.description = product.description ?: ""
                        productItem.sku = product.sku ?: ""
                        productItem.price = product.price ?: ""
                        productItem.iapViewType =
                            if (ProductType.SUBSCRIPTION == product.productType) IapViewType.SUB else IapViewType.IN_APP
                        if (ProductType.CONSUMABLE == product.productType) productItem.isBuy =
                            false else {
                            productItem.isBuy = skuUnavailables.contains(product.sku)
                        }
                        if (screenType == AmazonScreenType.BUY_GOLD) {
                            if (product.productType == ProductType.CONSUMABLE) {
                                productItems.add(productItem)
                            }
                        } else {
                            if (product.productType == ProductType.ENTITLED || product.productType == ProductType.SUBSCRIPTION) {
                                productItems.add(productItem)
                            }
                        }
                    }
                }
                notifyUpdateListView()
            }
            ProductDataResponse.RequestStatus.FAILED -> notifyUpdateListView()
            else -> {}
        }
    }

    /**
     * Khi purchase thành công
     */
    override fun onPurchaseResponse(purchaseResponse: PurchaseResponse) {
        when (purchaseResponse.requestStatus) {
            PurchaseResponse.RequestStatus.SUCCESSFUL -> {
                currentUserId = purchaseResponse.userData.userId
                currentMarketPlace = purchaseResponse.userData.marketplace
                PurchasingService.notifyFulfillment(
                    purchaseResponse.receipt.receiptId,
                    FulfillmentResult.FULFILLED
                )
                handleReceiptData(purchaseResponse.receipt)
                val receipt = purchaseResponse.receipt

                if (receipt.productType == ProductType.CONSUMABLE) {
                    productItems.map { prodItem ->
                        if (prodItem.sku == receipt.sku) {
                            checkOnAddMoney(receipt) {
                                MoneyManager.addMoney(prodItem.price)
                            }
                            return@map
                        }
                    }
                } else {
                    productItems.map { prodItem ->
                        val sku = if (receipt.productType == ProductType.SUBSCRIPTION) {
                            receipt.termSku
                        } else receipt.sku

                        if (sku.contains(prodItem.sku)) {
                            if (prodItem.sku.contains(AdsComponents.INSTANCE.billingId.interstitial)) {
                                AdsComponents.INSTANCE.adsPrefs.isBillingInterstitial = true
                            } else if (prodItem.sku.contains(AdsComponents.INSTANCE.billingId.banner)) {
                                AdsComponents.INSTANCE.adsPrefs.isBillingAdmobBanner = true
                            } else checkOnAddMoney(receipt) {
                                MoneyManager.addMoney(prodItem.price, DEFAULT_EXCHANGE_RATE_OTHER)
                            }
                            return@map
                        }
                    }
                }
            }
            PurchaseResponse.RequestStatus.FAILED -> {}
            else -> {}
        }
    }

    private fun checkOnAddMoney(receipt: Receipt, onNonContains: () -> Unit) {
        val skuId =
            if (receipt.productType == ProductType.SUBSCRIPTION) receipt.termSku else receipt.sku
        AdsComponents.INSTANCE.billingMappers.find {
            skuId.uppercase().contains(it.productId.uppercase())
        }?.let {
            MoneyManager.addMoney(it.refundMoney, 1.0)
        } ?: onNonContains.invoke()
    }

    /**
     * Trả về những item đã mua
     */
    override fun onPurchaseUpdatesResponse(purchaseUpdatesResponse: PurchaseUpdatesResponse) {
        when (purchaseUpdatesResponse.requestStatus) {
            PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL -> {
                currentUserId = purchaseUpdatesResponse.userData.userId
                currentMarketPlace = purchaseUpdatesResponse.userData.marketplace
                for (receipt in purchaseUpdatesResponse.receipts) {
                    // Grant Item to User
                    handleReceiptData(receipt)
                }
                if (purchaseUpdatesResponse.hasMore()) {
                    PurchasingService.getPurchaseUpdates(true)
                }
            }
            PurchaseUpdatesResponse.RequestStatus.FAILED -> {}
            else -> {}
        }
    }

    private fun handleReceiptData(receipt: Receipt) {
        receipt.productType?.let {
            when (it) {
                ProductType.CONSUMABLE -> {}
                ProductType.SUBSCRIPTION -> {
                    val subscriptionModel = SubscriptionModel()
                    subscriptionModel.userId = currentUserId
                    subscriptionModel.sku = receipt.sku
                    subscriptionModel.receiptId = receipt.receiptId
                    subscriptionModel.fromDate =
                        receipt.purchaseDate?.time ?: DbHelper.TO_DATE_NOT_SET
                    subscriptionModel.toDate = receipt.cancelDate?.time ?: DbHelper.TO_DATE_NOT_SET
                    productItems.find { receipt.termSku.contains(it.sku) }?.let { prodItem ->
                        prodItem.isBuy = true
                    }
                    doOnBackground {
                        AdsComponents.INSTANCE.dbHelper.insertSubscriptionRecord(subscriptionModel)
                    }
                }
                ProductType.ENTITLED -> {
                    val entitlementModel = EntitlementModel()
                    entitlementModel.userId = currentUserId
                    entitlementModel.sku = receipt.sku
                    entitlementModel.receiptId = receipt.receiptId
                    entitlementModel.purchaseDate =
                        receipt.purchaseDate?.time ?: DbHelper.TO_DATE_NOT_SET
                    entitlementModel.cancelDate =
                        receipt.cancelDate?.time ?: DbHelper.TO_DATE_NOT_SET
                    productItems.find { receipt.sku.contains(it.sku) }?.let { prodItem ->
                        prodItem.isBuy = true
                    }
                    doOnBackground {
                        AdsComponents.INSTANCE.dbHelper.insertEntitlementRecord(entitlementModel)
                    }
                }
            }

            notifyUpdateListView()
        }
    }

    private fun doOnBackground(doWork: () -> Unit) = CoroutineScope(Dispatchers.IO).launch {
        doWork.invoke()
    }

    fun getAllProductSkus(): Set<String> {
        return allProductSkus
    }

    fun setAllProductSkus(allProductSkus: MutableSet<String>) {
        this.allProductSkus = allProductSkus
    }

    fun getProductItems(): List<ProductItem> {
        return productItems
    }

    fun setProductItems(productItems: MutableList<ProductItem>) {
        this.productItems.clear()
        this.productItems.addAll(productItems)
    }
}
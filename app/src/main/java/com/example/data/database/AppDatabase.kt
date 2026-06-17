package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        CartItemEntity::class,
        WishlistItemEntity::class,
        OrderEntity::class,
        LegalPageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun legalPageDao(): LegalPageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tshirt_zone_db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateProducts(database.productDao())
                    populateLegalPages(database.legalPageDao())
                }
            }
        }

        private suspend fun populateProducts(productDao: ProductDao) {
            val defaultProducts = listOf(
                ProductEntity(
                    id = 1,
                    title = "Royal Gold Lion Mandala T-Shirt",
                    titleBn = "রয়েল গোল্ড লায়ন ম্যান্ডালা টি-শার্ট",
                    description = "Premium heavyweight, 100% fine combed cotton. Features an intricate metallic golden lion mandala print, high fashion executive cut, non-fade premium golden vinyl finish.",
                    descriptionBn = "১০০% ফাইন কম্বড সুতির ভারী ও প্রিমিয়াম কাপড়। বুকে সোনালি লায়ন ম্যান্ডালার রাজকীয় সুচারু প্রিন্ট। হাই-ফ্যাশন রিটেল ফিটিং, যা সহজে নষ্ট হবে না।",
                    price = 1250.0,
                    category = "Premium Embroidery",
                    categoryBn = "প্রিমিয়াম এমব্রয়ডারি",
                    imageUrl = "img_premium_gold_tshirt",
                    isPremium = true,
                    rating = 4.9,
                    stock = 25,
                    sizeAvailable = "M,L,XL,XXL"
                ),
                ProductEntity(
                    id = 2,
                    title = "Dhaka Retro Street Signature T-Shirt",
                    titleBn = "ঢাকা রেট্রো স্ট্রিট সিগনেচার টি-শার্ট",
                    description = "Our classic urban streetwear aesthetic. Featuring stylish gold-gilded Bangla calligraphy typography of Dhaka skyline overlay. Super soft and heavy cotton blend.",
                    descriptionBn = "শহুরে জীবনের অনন্য স্ট্রিটওয়্যার ডিজাইন। কালো কাপড়ে জিল্ডিং সোনালি হরফে 'ঢাকা' ক্যালিগ্রাফি ও ব্যাকগ্রাউন্ডে শহরের অবয়ব ফুটিয়ে তোলা হয়েছে। অত্যন্ত নরম ও আরামদায়ক কাপড়ে তৈরি।",
                    price = 850.0,
                    category = "Streetwear Collection",
                    categoryBn = "স্ট্রিটওয়্যার কালেকশন",
                    imageUrl = "img_tshirt_hero",
                    isPremium = false,
                    rating = 4.7,
                    stock = 45,
                    sizeAvailable = "S,M,L,XL"
                ),
                ProductEntity(
                    id = 3,
                    title = "Cyber Gold Abstract Geometric Apparel",
                    titleBn = "সাইবার গোল্ড অ্যাবস্ট্রাক্ট টি-শার্ট",
                    description = "Futuristic neon gold abstract circuitry embossing. Beautiful premium quality fabric for outstanding durability and luxury touch.",
                    descriptionBn = "ফিউচারিস্টিক সোনালি রঙের সাইবার কারিগরি ও জ্যামিতিক নিখুঁত প্রিন্ট এমবসিং। স্থায়ী কালার ও অত্যন্ত অভিজাত লুক দিবে এই টি-শার্টটি।",
                    price = 990.0,
                    category = "Cyberpunk Series",
                    categoryBn = "সাইবারপাঙ্ক সিরিজ",
                    imageUrl = "img_premium_gold_tshirt",
                    isPremium = false,
                    rating = 4.8,
                    stock = 18,
                    sizeAvailable = "M,L,XL"
                ),
                ProductEntity(
                    id = 4,
                    title = "Arabic Calligraphy Sacred Hope T-Shirt",
                    titleBn = "আরবি ক্যালিগ্রাফি সেকরেড হোপ টি-শার্ট",
                    description = "Stunning luxury black cotton t-shirt with calligraphy denoting 'Peace and Hope' beautifully embossed in shimmering metallic gold paint.",
                    descriptionBn = "দারুণ রুচিশীল নরম কালো কাপড়ে 'শান্তি ও আশা' বোঝানো ঐতিহ্যবাহী আরবী ক্যালিগ্রাফি মেটালিক গোল্ড কালিতে এমবস প্রিন্ট করা। শতভাগ আরামদায়ক।",
                    price = 1150.0,
                    category = "Calligraphy Gold",
                    categoryBn = "ক্যালিগ্রাফি গোল্ড",
                    imageUrl = "img_tshirt_hero",
                    isPremium = true,
                    rating = 4.9,
                    stock = 14,
                    sizeAvailable = "M,L,XL,XXL"
                )
            )
            productDao.insertProducts(defaultProducts)
        }

        private suspend fun populateLegalPages(legalPageDao: LegalPageDao) {
            val legalPages = listOf(
                LegalPageEntity(
                    id = "privacy",
                    titleEn = "Privacy Policy",
                    titleBn = "গোপনীয়তা নীতি",
                    contentEn = "<h3>1. Information We Collect</h3><p>At T-Shirt Zone BD, we value your privacy. We collect personal information such as name, phone number, and delivery address to deliver premium orders properly.</p><h3>2. Data Security</h3><p>Your delivery and transaction security is handled with utmost security encryption using standard offline firestore-like local database configurations. We do not sell or leak information.</p>",
                    contentBn = "<h3>১. সংগৃহীত তথ্য</h3><p>টি-শার্ট জোন বিডি-তে আমরা আপনার গোপনীয়তাকে সর্বোচ্চ মূল্যায়ন করি। আপনার অর্ডার ডেলিভারি করতে আমরা নাম, মোবাইল নম্বর এবং ঠিকানা সংরক্ষণ করি।</p><h3>২. ডেটা নিরাপত্তা</h3><p>আপনার অর্ডার ও আর্থিক লেনদেন আধুনিক অফলাইন সিকিউরিটির মাধ্যমে সুরক্ষিত রাখা হয়। আমরা কোনো থার্ড পার্টির কাছে তথ্য বিক্রি করি না।</p>"
                ),
                LegalPageEntity(
                    id = "refund",
                    titleEn = "Refund and Return Policy",
                    titleBn = "রিফান্ড ও রিটার্ন নীতি",
                    contentEn = "<h3>Our Guarantee</h3><p>If you find any manufacturing defect, fitting issue, or color deterioration within 7 days of receiving your T-Shirt Zone BD product, we will exchange or refund 100% of your money. Items must be unworn and in original packaging.</p>",
                    contentBn = "<h3>আমাদের গ্যারান্টি</h3><p>টি-শার্ট জোন বিডি-র ডেলিভারি পাওয়ার ৭ দিনের মধ্যে যদি কোনো প্রিন্ট ক্রুটি, ছেঁড়া কাপড় বা সাইজ মেলানোতে সমস্যা পাওয়া যায়, তবে আমরা ১০০% ক্যাশব্যাক বা পরিবর্তন করে দেব। পোশাক অব্যবহৃত অবস্থায় থাকতে হবে।</p>"
                ),
                LegalPageEntity(
                    id = "terms",
                    titleEn = "Terms and Conditions",
                    titleBn = "শর্তাবলী ও নিয়মাবলী",
                    contentEn = "<h3>1. Orders and Acceptance</h3><p>All orders placed via bKash, Nagad, Rocket or Cash On Delivery (COD) are processed securely inside Bangladesh. Stock management is kept interactive.</p><h3>2. Delivery Timelines</h3><p>Dhaka: 24 to 48 hours. Outside Dhaka: 2 to 4 working days.</p>",
                    contentBn = "<h3>১. অর্ডার ও প্রক্রিয়া</h3><p>বিকাশ, নগদ, রকেট বা ক্যাশ অন ডেলিভারি (সিওডি) এর মাধ্যমে সব অর্ডার প্রসেস করা হয়। অর্ডার দ্রুততার সাথে ডেলিভারি করা হয়।</p><h3>২. ডেলিভারি সময়</h3><p>ঢাকা সিটি: ২৪ থেকে ৪৮ ঘণ্টা। ঢাকার বাইরে: ২ থেকে ৪ কার্য দিবস।</p>"
                )
            )
            for (page in legalPages) {
                legalPageDao.insertLegalPage(page)
            }
        }
    }
}

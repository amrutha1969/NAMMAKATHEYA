package com.example.nammakathey

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import kotlin.math.roundToInt

private val Orange = Color(0xFFFF6B35)
private val Blue = Color(0xFF2F80ED)
private val Green = Color(0xFF27AE60)
private val Yellow = Color(0xFFFFD166)
private val Cream = Color(0xFFFFF8E1)
private val Ink = Color(0xFF2F2F2F)

class NammaKatheyApp : Application() {
    val database by lazy { NammaKatheyDatabase.getDatabase(this) }
    val repository by lazy { NammaKatheyRepository(database, this) }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NammaKatheyRoot() }
    }
}

@Entity(tableName = "districts")
data class District(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "district_name_english") val districtNameEnglish: String,
    @ColumnInfo(name = "district_name_kannada") val districtNameKannada: String,
    @ColumnInfo(name = "district_code") val districtCode: String,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "hero_count") val heroCount: Int = 0
)

@Entity(tableName = "heroes")
data class Hero(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "hero_name_english") val heroNameEnglish: String,
    @ColumnInfo(name = "hero_name_kannada") val heroNameKannada: String,
    @ColumnInfo(name = "district_code") val districtCode: String,
    val category: String,
    @ColumnInfo(name = "birth_year") val birthYear: String,
    @ColumnInfo(name = "death_year") val deathYear: String?,
    @ColumnInfo(name = "short_description_english") val shortDescriptionEnglish: String,
    @ColumnInfo(name = "short_description_kannada") val shortDescriptionKannada: String,
    @ColumnInfo(name = "hero_image") val heroImage: String = "",
    @ColumnInfo(name = "is_bookmarked") val isBookmarked: Boolean = false,
    @ColumnInfo(name = "is_user_created") val isUserCreated: Boolean = false
)

@Entity(tableName = "story_pages")
data class StoryPage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "hero_id") val heroId: Int,
    @ColumnInfo(name = "page_number") val pageNumber: Int,
    @ColumnInfo(name = "content_english") val contentEnglish: String,
    @ColumnInfo(name = "content_kannada") val contentKannada: String,
    @ColumnInfo(name = "illustration_image") val illustrationImage: String = "",
    @ColumnInfo(name = "audio_file_english") val audioFileEnglish: String? = null,
    @ColumnInfo(name = "audio_file_kannada") val audioFileKannada: String? = null
)

@Entity(tableName = "quiz_questions")
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "hero_id") val heroId: Int,
    @ColumnInfo(name = "question_english") val questionEnglish: String,
    @ColumnInfo(name = "question_kannada") val questionKannada: String,
    @ColumnInfo(name = "option1_english") val option1English: String,
    @ColumnInfo(name = "option1_kannada") val option1Kannada: String,
    @ColumnInfo(name = "option2_english") val option2English: String,
    @ColumnInfo(name = "option2_kannada") val option2Kannada: String,
    @ColumnInfo(name = "option3_english") val option3English: String,
    @ColumnInfo(name = "option3_kannada") val option3Kannada: String,
    @ColumnInfo(name = "option4_english") val option4English: String,
    @ColumnInfo(name = "option4_kannada") val option4Kannada: String,
    @ColumnInfo(name = "correct_answer") val correctAnswer: Int
)

@Entity(tableName = "memorials")
data class Memorial(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "hero_id") val heroId: Int,
    @ColumnInfo(name = "memorial_name_english") val memorialNameEnglish: String,
    @ColumnInfo(name = "memorial_name_kannada") val memorialNameKannada: String,
    @ColumnInfo(name = "address_english") val addressEnglish: String,
    @ColumnInfo(name = "address_kannada") val addressKannada: String,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "memorial_image") val memorialImage: String = "",
    @ColumnInfo(name = "is_user_created") val isUserCreated: Boolean = false
)

@Entity(tableName = "user_badges")
data class UserBadge(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "hero_id") val heroId: Int,
    @ColumnInfo(name = "earned_date") val earnedDate: Long,
    @ColumnInfo(name = "quiz_score") val quizScore: Int
)

data class BadgeWithHero(
    val id: Int,
    val heroId: Int,
    val heroNameEnglish: String,
    val heroNameKannada: String,
    val score: Int,
    val earnedDate: Long
)

@Dao
interface DistrictDao {
    @Query("SELECT * FROM districts ORDER BY district_name_english")
    fun getAllDistricts(): Flow<List<District>>

    @Query("SELECT * FROM districts WHERE district_name_english LIKE :query OR district_name_kannada LIKE :query ORDER BY district_name_english")
    fun searchDistricts(query: String): Flow<List<District>>

    @Query("SELECT * FROM districts WHERE district_code = :districtCode LIMIT 1")
    suspend fun byCode(districtCode: String): District?

    @Query("SELECT COUNT(*) FROM districts")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<District>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: District): Long

    @Update
    suspend fun update(item: District)

    @Query("DELETE FROM districts WHERE district_code = :districtCode")
    suspend fun deleteByCode(districtCode: String)
}

@Dao
interface HeroDao {
    @Query("SELECT * FROM heroes WHERE district_code = :districtCode ORDER BY hero_name_english")
    fun byDistrict(districtCode: String): Flow<List<Hero>>

    @Query("SELECT * FROM heroes WHERE district_code = :districtCode ORDER BY hero_name_english")
    suspend fun byDistrictOnce(districtCode: String): List<Hero>

    @Query("SELECT * FROM heroes WHERE district_code = :districtCode AND category = :category ORDER BY hero_name_english")
    fun byCategory(districtCode: String, category: String): Flow<List<Hero>>

    @Query("SELECT * FROM heroes WHERE id = :id LIMIT 1")
    suspend fun byId(id: Int): Hero?

    @Query("SELECT * FROM heroes WHERE id = :id LIMIT 1")
    fun observeById(id: Int): Flow<Hero?>

    @Query("SELECT * FROM heroes WHERE is_bookmarked = 1 ORDER BY hero_name_english")
    fun bookmarks(): Flow<List<Hero>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Hero>)

    @Insert
    suspend fun insert(hero: Hero): Long

    @Update
    suspend fun update(hero: Hero)

    @Query("DELETE FROM heroes WHERE id = :id AND is_user_created = 1")
    suspend fun deleteUserHero(id: Int)

    @Query("DELETE FROM heroes WHERE district_code = :districtCode")
    suspend fun deleteByDistrict(districtCode: String)
}

@Dao
interface StoryPageDao {
    @Query("SELECT * FROM story_pages WHERE hero_id = :heroId ORDER BY page_number")
    fun byHero(heroId: Int): Flow<List<StoryPage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<StoryPage>)

    @Query("DELETE FROM story_pages WHERE hero_id = :heroId")
    suspend fun deleteForHero(heroId: Int)
}

@Dao
interface QuizQuestionDao {
    @Query("SELECT * FROM quiz_questions WHERE hero_id = :heroId ORDER BY id LIMIT 3")
    fun byHero(heroId: Int): Flow<List<QuizQuestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<QuizQuestion>)

    @Query("DELETE FROM quiz_questions WHERE hero_id = :heroId")
    suspend fun deleteForHero(heroId: Int)
}

@Dao
interface MemorialDao {
    @Query("SELECT * FROM memorials WHERE hero_id = :heroId ORDER BY memorial_name_english")
    fun byHero(heroId: Int): Flow<List<Memorial>>

    @Query("SELECT * FROM memorials ORDER BY memorial_name_english")
    fun all(): Flow<List<Memorial>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Memorial>)

    @Insert
    suspend fun insert(item: Memorial): Long

    @Query("DELETE FROM memorials WHERE id = :id AND is_user_created = 1")
    suspend fun deleteUserMemorial(id: Int)

    @Query("DELETE FROM memorials WHERE hero_id = :heroId AND is_user_created = 1")
    suspend fun deleteUserMemorialsForHero(heroId: Int)

    @Query("DELETE FROM memorials WHERE hero_id = :heroId")
    suspend fun deleteForHero(heroId: Int)
}

@Dao
interface UserBadgeDao {
    @Query("SELECT user_badges.id, user_badges.hero_id AS heroId, heroes.hero_name_english AS heroNameEnglish, heroes.hero_name_kannada AS heroNameKannada, user_badges.quiz_score AS score, user_badges.earned_date AS earnedDate FROM user_badges INNER JOIN heroes ON heroes.id = user_badges.hero_id ORDER BY earnedDate DESC")
    fun allWithHero(): Flow<List<BadgeWithHero>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: UserBadge)

    @Query("DELETE FROM user_badges WHERE hero_id = :heroId")
    suspend fun deleteForHero(heroId: Int)
}

@Database(
    entities = [District::class, Hero::class, StoryPage::class, QuizQuestion::class, Memorial::class, UserBadge::class],
    version = 3,
    exportSchema = false
)
abstract class NammaKatheyDatabase : RoomDatabase() {
    abstract fun districtDao(): DistrictDao
    abstract fun heroDao(): HeroDao
    abstract fun storyPageDao(): StoryPageDao
    abstract fun quizQuestionDao(): QuizQuestionDao
    abstract fun memorialDao(): MemorialDao
    abstract fun userBadgeDao(): UserBadgeDao

    companion object {
        @Volatile private var instance: NammaKatheyDatabase? = null
        fun getDatabase(context: Context): NammaKatheyDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, NammaKatheyDatabase::class.java, "namma_kathey.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}

class NammaKatheyRepository(private val db: NammaKatheyDatabase, private val context: Context) {
    private val gson = Gson()

    fun districts(query: String): Flow<List<District>> =
        if (query.isBlank()) db.districtDao().getAllDistricts() else db.districtDao().searchDistricts("%$query%")

    fun heroes(districtCode: String, category: String): Flow<List<Hero>> =
        if (category == "ALL") db.heroDao().byDistrict(districtCode) else db.heroDao().byCategory(districtCode, category)

    fun hero(id: Int): Flow<Hero?> = db.heroDao().observeById(id)
    fun story(heroId: Int): Flow<List<StoryPage>> = db.storyPageDao().byHero(heroId)
    fun quiz(heroId: Int): Flow<List<QuizQuestion>> = db.quizQuestionDao().byHero(heroId)
    fun memorials(heroId: Int): Flow<List<Memorial>> = db.memorialDao().byHero(heroId)
    fun allMemorials(): Flow<List<Memorial>> = db.memorialDao().all()
    fun bookmarks(): Flow<List<Hero>> = db.heroDao().bookmarks()
    fun badges(): Flow<List<BadgeWithHero>> = db.userBadgeDao().allWithHero()

    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (db.districtDao().count() == 0) {
            val districts = loadDistricts()
            val heroes = loadHeroes()
            val explicitQuestions = loadQuizQuestions()
            db.districtDao().insertAll(districts)
            db.heroDao().insertAll(heroes)
            db.storyPageDao().insertAll(expandStoryPages(loadStoryPages(), heroes, districts))
            db.quizQuestionDao().insertAll(explicitQuestions + generatedQuestionsForMissingHeroes(heroes, explicitQuestions, districts))
            db.memorialDao().insertAll(loadMemorials())
        }
    }

    suspend fun toggleBookmark(heroId: Int) = withContext(Dispatchers.IO) {
        db.heroDao().byId(heroId)?.let { db.heroDao().update(it.copy(isBookmarked = !it.isBookmarked)) }
    }

    suspend fun saveBadge(heroId: Int, score: Int) = withContext(Dispatchers.IO) {
        db.userBadgeDao().deleteForHero(heroId)
        db.userBadgeDao().insert(UserBadge(heroId = heroId, earnedDate = System.currentTimeMillis(), quizScore = score))
    }

    suspend fun addHero(input: HeroInput) = withContext(Dispatchers.IO) {
        val id = db.heroDao().insert(
            Hero(
                heroNameEnglish = input.nameEnglish,
                heroNameKannada = input.nameKannada.ifBlank { input.nameEnglish },
                districtCode = input.districtCode,
                category = input.category,
                birthYear = input.birthYear,
                deathYear = input.deathYear.ifBlank { null },
                shortDescriptionEnglish = input.descriptionEnglish,
                shortDescriptionKannada = input.descriptionKannada.ifBlank { input.descriptionEnglish },
                heroImage = input.imagePath,
                isUserCreated = true
            )
        ).toInt()
        db.storyPageDao().insertAll(input.storyPages.mapIndexed { index, text ->
            StoryPage(heroId = id, pageNumber = index + 1, contentEnglish = text, contentKannada = text)
        })
        db.quizQuestionDao().insertAll(defaultQuestions(id, input.nameEnglish, input.nameKannada))
    }

    suspend fun addDistrict(input: DistrictInput) = withContext(Dispatchers.IO) {
        val nameEnglish = input.nameEnglish.trim()
        val districtCode = input.districtCode.trim().ifBlank { districtCodeFromName(nameEnglish) }
        val existing = db.districtDao().byCode(districtCode)
        val district = District(
            id = existing?.id ?: 0,
            districtNameEnglish = nameEnglish,
            districtNameKannada = input.nameKannada.trim().ifBlank { nameEnglish },
            districtCode = districtCode,
            latitude = input.latitude.toDoubleOrNull() ?: 0.0,
            longitude = input.longitude.toDoubleOrNull() ?: 0.0,
            heroCount = input.heroCount.toIntOrNull() ?: 0
        )
        if (existing == null) db.districtDao().insert(district) else db.districtDao().update(district)
    }

    suspend fun deleteDistrict(districtCode: String) = withContext(Dispatchers.IO) {
        val heroes = db.heroDao().byDistrictOnce(districtCode)
        heroes.forEach { hero ->
            db.storyPageDao().deleteForHero(hero.id)
            db.quizQuestionDao().deleteForHero(hero.id)
            db.memorialDao().deleteForHero(hero.id)
            db.userBadgeDao().deleteForHero(hero.id)
        }
        db.heroDao().deleteByDistrict(districtCode)
        db.districtDao().deleteByCode(districtCode)
    }

    private fun districtCodeFromName(name: String): String {
        val letters = name
            .uppercase(Locale.ENGLISH)
            .filter { it.isLetterOrDigit() }
            .take(8)
        return letters.ifBlank { "DIST${System.currentTimeMillis().toString().takeLast(4)}" }
    }

    suspend fun updateHeroImage(heroId: Int, imagePath: String) = withContext(Dispatchers.IO) {
        db.heroDao().byId(heroId)?.let { db.heroDao().update(it.copy(heroImage = imagePath)) }
    }

    suspend fun deleteHero(heroId: Int) = withContext(Dispatchers.IO) {
        db.storyPageDao().deleteForHero(heroId)
        db.quizQuestionDao().deleteForHero(heroId)
        db.memorialDao().deleteUserMemorialsForHero(heroId)
        db.userBadgeDao().deleteForHero(heroId)
        db.heroDao().deleteUserHero(heroId)
    }

    suspend fun addMemorial(item: Memorial) = withContext(Dispatchers.IO) { db.memorialDao().insert(item.copy(isUserCreated = true)) }
    suspend fun deleteMemorial(id: Int) = withContext(Dispatchers.IO) { db.memorialDao().deleteUserMemorial(id) }

    private fun readAsset(name: String): String =
        context.assets.open(name).bufferedReader().use { it.readText() }

    private fun loadDistricts(): List<District> =
        gson.fromJson(readAsset("districts.json"), Array<District>::class.java).toList()

    private fun loadHeroes(): List<Hero> =
        gson.fromJson(readAsset("heroes.json"), Array<Hero>::class.java)
            .toList()
            .map { it.copy(heroImage = it.heroImage.orEmpty()) }

    private fun loadStoryPages(): List<StoryPage> =
        gson.fromJson(readAsset("story_pages.json"), Array<StoryPage>::class.java)
            .toList()
            .map { it.copy(illustrationImage = it.illustrationImage.orEmpty()) }

    private fun loadQuizQuestions(): List<QuizQuestion> =
        gson.fromJson(readAsset("quiz_questions.json"), Array<QuizQuestion>::class.java).toList()

    private fun loadMemorials(): List<Memorial> =
        gson.fromJson(readAsset("memorials.json"), Array<Memorial>::class.java)
            .toList()
            .map { it.copy(memorialImage = it.memorialImage.orEmpty()) }

    private fun expandStoryPages(
        pages: List<StoryPage>,
        heroes: List<Hero>,
        districts: List<District>
    ): List<StoryPage> {
        val heroById = heroes.associateBy { it.id }
        val districtByCode = districts.associateBy { it.districtCode }
        return pages.map { page ->
            val hero = heroById[page.heroId] ?: return@map page
            val district = districtByCode[hero.districtCode]
            val categoryEnglish = hero.category.replace('_', ' ').lowercase()
            val categoryKannada = when (hero.category) {
                "FREEDOM_FIGHTER" -> "ಸ್ವಾತಂತ್ರ್ಯ ಮತ್ತು ಧೈರ್ಯದ ಕಥೆ"
                "POET" -> "ಕಾವ್ಯ ಮತ್ತು ಚಿಂತನೆಯ ಕಥೆ"
                "SOCIAL_REFORMER" -> "ಸಮಾಜ ಸುಧಾರಣೆ ಮತ್ತು ಮೌಲ್ಯಗಳ ಕಥೆ"
                else -> "ಪರಂಪರೆಯ ಕಥೆ"
            }
            val districtEnglish = district?.districtNameEnglish ?: hero.districtCode
            val districtKannada = district?.districtNameKannada ?: hero.districtCode
            val englishExpansion = when (page.pageNumber) {
                1 -> " In ${districtEnglish}, children can imagine the lanes, fields, homes, and gathering places where this story still lives in memory. ${hero.heroNameEnglish}'s early journey was not only about one brave moment; it was shaped by family, community, discipline, questions, and the small choices that prepare a person to do something meaningful. As you read, think about how a local hero begins like any ordinary person, but grows stronger by caring deeply about people and place."
                2 -> " This part of the story shows the challenge that tested ${hero.heroNameEnglish}. A ${categoryEnglish} does not become important only because of fame, but because of decisions made when things are confusing, unfair, or difficult. The people around the hero also matter: friends, teachers, soldiers, villagers, writers, workers, and families all carry the story forward. That is why local history belongs to everyone, not only to kings and textbooks."
                else -> " The lesson from ${hero.heroNameEnglish} reaches beyond one village or one year. It asks children to notice courage in daily life: speaking truth, helping someone weaker, studying with care, respecting language, protecting nature, and remembering elders' stories. When children know heroes from ${districtEnglish}, they build pride that is gentle, thoughtful, and useful. The badge you earn is a reminder to carry that value into your own actions."
            }
            val kannadaExpansion = when (page.pageNumber) {
                1 -> " ${districtKannada} ಜಿಲ್ಲೆಯಲ್ಲಿ ಈ ಕಥೆ ಇನ್ನೂ ನೆನಪಿನಲ್ಲಿ ಬದುಕುತ್ತಿರುವ ಬೀದಿಗಳು, ಹೊಲಗಳು, ಮನೆಗಳು ಮತ್ತು ಜನರು ಸೇರುವ ಸ್ಥಳಗಳನ್ನು ಮಕ್ಕಳು ಕಲ್ಪಿಸಬಹುದು. ${hero.heroNameKannada} ಅವರ ಆರಂಭಿಕ ಪಯಣ ಒಂದು ಧೈರ್ಯದ ಕ್ಷಣ ಮಾತ್ರವಲ್ಲ; ಕುಟುಂಬ, ಸಮುದಾಯ, ಶಿಸ್ತು, ಪ್ರಶ್ನೆಗಳು ಮತ್ತು ಅರ್ಥಪೂರ್ಣ ಕೆಲಸಕ್ಕೆ ತಯಾರು ಮಾಡುವ ಸಣ್ಣ ಆಯ್ಕೆಗಳು ಅದನ್ನು ರೂಪಿಸಿದವು. ಸ್ಥಳೀಯ ವೀರರು ಸಾಮಾನ್ಯ ವ್ಯಕ್ತಿಗಳಂತೆ ಆರಂಭಿಸಿ ಜನರು ಮತ್ತು ನಾಡಿನ ಬಗ್ಗೆ ಆಳವಾದ ಕಾಳಜಿಯಿಂದ ಬಲಿಷ್ಠರಾಗುತ್ತಾರೆ."
                2 -> " ಈ ಭಾಗವು ${hero.heroNameKannada} ಅವರನ್ನು ಪರೀಕ್ಷಿಸಿದ ಸವಾಲನ್ನು ತೋರಿಸುತ್ತದೆ. ${categoryKannada} ಪ್ರಸಿದ್ಧಿಯ ಕಾರಣದಿಂದ ಮಾತ್ರ ಮಹತ್ವದಾಗುವುದಿಲ್ಲ; ಗೊಂದಲ, ಅನ್ಯಾಯ ಅಥವಾ ಕಷ್ಟದ ಸಮಯದಲ್ಲಿ ತೆಗೆದುಕೊಳ್ಳುವ ನಿರ್ಧಾರಗಳಿಂದ ಅದು ಮಹತ್ವ ಪಡೆಯುತ್ತದೆ. ವೀರರ ಸುತ್ತಲಿನ ಜನರೂ ಮುಖ್ಯರು: ಸ್ನೇಹಿತರು, ಗುರುಗಳು, ಸೈನಿಕರು, ಹಳ್ಳಿಯವರು, ಬರಹಗಾರರು, ಕಾರ್ಮಿಕರು ಮತ್ತು ಕುಟುಂಬಗಳು ಕಥೆಯನ್ನು ಮುಂದಕ್ಕೆ ಹೊತ್ತುಕೊಂಡು ಹೋಗುತ್ತಾರೆ."
                else -> " ${hero.heroNameKannada} ಅವರ ಪಾಠ ಒಂದು ಹಳ್ಳಿ ಅಥವಾ ಒಂದು ವರ್ಷವನ್ನು ಮೀರಿ ಮಕ್ಕಳ ಹೃದಯಕ್ಕೆ ತಲುಪುತ್ತದೆ. ಸತ್ಯ ಹೇಳುವುದು, ದುರ್ಬಲರಿಗೆ ಸಹಾಯಮಾಡುವುದು, ಶ್ರದ್ಧೆಯಿಂದ ಓದುವುದು, ಭಾಷೆಯನ್ನು ಗೌರವಿಸುವುದು, ಪ್ರಕೃತಿಯನ್ನು ಕಾಪಾಡುವುದು ಮತ್ತು ಹಿರಿಯರ ಕಥೆಗಳನ್ನು ನೆನಪಿಡುವುದು ಕೂಡ ಧೈರ್ಯವೇ ಎಂದು ಇದು ತಿಳಿಸುತ್ತದೆ. ${districtKannada} ಜಿಲ್ಲೆಯ ವೀರರನ್ನು ತಿಳಿದಾಗ ಮಕ್ಕಳು ಮೃದುವಾದ, ಚಿಂತನೆಯುತ ಮತ್ತು ಉಪಯುಕ್ತವಾದ ಹೆಮ್ಮೆಯನ್ನು ಬೆಳೆಸುತ್ತಾರೆ."
            }
            page.copy(
                contentEnglish = page.contentEnglish + englishExpansion,
                contentKannada = page.contentKannada + kannadaExpansion
            )
        }
    }

    private fun generatedQuestionsForMissingHeroes(
        heroes: List<Hero>,
        explicitQuestions: List<QuizQuestion>,
        districts: List<District>
    ): List<QuizQuestion> {
        val heroesWithQuestions = explicitQuestions.map { it.heroId }.toSet()
        val districtByCode = districts.associateBy { it.districtCode }
        return heroes
            .filter { it.id !in heroesWithQuestions }
            .flatMap { hero ->
                val district = districtByCode[hero.districtCode]
                val districtEnglish = district?.districtNameEnglish ?: hero.districtCode
                val districtKannada = district?.districtNameKannada ?: hero.districtCode
                listOf(
                    QuizQuestion(
                        heroId = hero.id,
                        questionEnglish = "Which hero did you read about?",
                        questionKannada = "ನೀವು ಯಾವ ವೀರರ ಬಗ್ಗೆ ಓದಿದಿರಿ?",
                        option1English = hero.heroNameEnglish,
                        option1Kannada = hero.heroNameKannada,
                        option2English = "Kittur Chennamma",
                        option2Kannada = "ಕಿತ್ತೂರು ಚೆನ್ನಮ್ಮ",
                        option3English = "Kempegowda",
                        option3Kannada = "ಕೆಂಪೇಗೌಡ",
                        option4English = "Kuvempu",
                        option4Kannada = "ಕುವೆಂಪು",
                        correctAnswer = 1
                    ),
                    QuizQuestion(
                        heroId = hero.id,
                        questionEnglish = "Which district is connected to ${hero.heroNameEnglish}?",
                        questionKannada = "${hero.heroNameKannada} ಅವರಿಗೆ ಯಾವ ಜಿಲ್ಲೆ ಸಂಬಂಧಿಸಿದೆ?",
                        option1English = districtEnglish,
                        option1Kannada = districtKannada,
                        option2English = "Bidar",
                        option2Kannada = "ಬೀದರ್",
                        option3English = "Gadag",
                        option3Kannada = "ಗದಗ",
                        option4English = "Ballari",
                        option4Kannada = "ಬಳ್ಳಾರಿ",
                        correctAnswer = 1
                    ),
                    QuizQuestion(
                        heroId = hero.id,
                        questionEnglish = "What can children learn from local heroes?",
                        questionKannada = "ಸ್ಥಳೀಯ ವೀರರಿಂದ ಮಕ್ಕಳು ಏನು ಕಲಿಯಬಹುದು?",
                        option1English = "Courage and values",
                        option1Kannada = "ಧೈರ್ಯ ಮತ್ತು ಮೌಲ್ಯಗಳು",
                        option2English = "To forget history",
                        option2Kannada = "ಇತಿಹಾಸ ಮರೆತುವುದು",
                        option3English = "To avoid reading",
                        option3Kannada = "ಓದನ್ನು ತಪ್ಪಿಸುವುದು",
                        option4English = "Nothing useful",
                        option4Kannada = "ಉಪಯೋಗವಿಲ್ಲ",
                        correctAnswer = 1
                    )
                )
            }
    }

    private fun defaultQuestions(heroId: Int, en: String, kn: String) = listOf(
        QuizQuestion(heroId = heroId, questionEnglish = "Which hero did you just add?", questionKannada = "ನೀವು ಈಗ ಯಾವ ವೀರರನ್ನು ಸೇರಿಸಿದ್ದೀರಿ?", option1English = en, option1Kannada = kn, option2English = "Kittur Chennamma", option2Kannada = "ಕಿತ್ತೂರು ಚೆನ್ನಮ್ಮ", option3English = "Sangolli Rayanna", option3Kannada = "ಸಂಗೊಳ್ಳಿ ರಾಯಣ್ಣ", option4English = "Basavanna", option4Kannada = "ಬಸವಣ್ಣ", correctAnswer = 1),
        QuizQuestion(heroId = heroId, questionEnglish = "What does a local hero teach us?", questionKannada = "ಸ್ಥಳೀಯ ವೀರರು ನಮಗೆ ಏನು ಕಲಿಸುತ್ತಾರೆ?", option1English = "Courage", option1Kannada = "ಧೈರ್ಯ", option2English = "Laziness", option2Kannada = "ಸೋಮಾರಿತನ", option3English = "Fear", option3Kannada = "ಭಯ", option4English = "Forgetfulness", option4Kannada = "ಮರೆವು", correctAnswer = 1),
        QuizQuestion(heroId = heroId, questionEnglish = "Why should we preserve local stories?", questionKannada = "ನಾವು ಸ್ಥಳೀಯ ಕಥೆಗಳನ್ನು ಏಕೆ ಉಳಿಸಬೇಕು?", option1English = "To build pride and values", option1Kannada = "ಹೆಮ್ಮೆ ಮತ್ತು ಮೌಲ್ಯಗಳನ್ನು ಬೆಳೆಸಲು", option2English = "To hide history", option2Kannada = "ಇತಿಹಾಸ ಮುಚ್ಚಲು", option3English = "To avoid learning", option3Kannada = "ಕಲಿಕೆಯನ್ನು ತಪ್ಪಿಸಲು", option4English = "No reason", option4Kannada = "ಕಾರಣ ಇಲ್ಲ", correctAnswer = 1)
    )
}

data class HeroInput(
    val districtCode: String,
    val nameEnglish: String,
    val nameKannada: String,
    val category: String,
    val birthYear: String,
    val deathYear: String,
    val descriptionEnglish: String,
    val descriptionKannada: String,
    val storyPages: List<String>,
    val imagePath: String
)

data class DistrictInput(
    val nameEnglish: String,
    val nameKannada: String,
    val districtCode: String,
    val latitude: String,
    val longitude: String,
    val heroCount: String
)

class MainViewModel(private val repository: NammaKatheyRepository) : ViewModel() {
    val language = MutableStateFlow("kn")
    val search = MutableStateFlow("")
    val category = MutableStateFlow("ALL")
    val districts = search.flatMapLatest { repository.districts(it) }.stateInVm(emptyList())
    val badges = repository.badges().stateInVm(emptyList())
    val bookmarks = repository.bookmarks().stateInVm(emptyList())
    val memorials = repository.allMemorials().stateInVm(emptyList())

    fun initialize() = launch { repository.initialize() }
    fun heroes(districtCode: String): StateFlow<List<Hero>> = category.flatMapLatest { repository.heroes(districtCode, it) }.stateInVm(emptyList())
    fun hero(id: Int): StateFlow<Hero?> = repository.hero(id).stateInVm(null)
    fun story(id: Int): StateFlow<List<StoryPage>> = repository.story(id).stateInVm(emptyList())
    fun quiz(id: Int): StateFlow<List<QuizQuestion>> = repository.quiz(id).stateInVm(emptyList())
    fun memorials(id: Int): StateFlow<List<Memorial>> = repository.memorials(id).stateInVm(emptyList())
    fun toggleLanguage() { language.value = if (language.value == "kn") "en" else "kn" }
    fun toggleBookmark(heroId: Int) = launch { repository.toggleBookmark(heroId) }
    fun saveBadge(heroId: Int, score: Int) = launch { repository.saveBadge(heroId, score) }
    fun addHero(input: HeroInput) = launch { repository.addHero(input) }
    fun addDistrict(input: DistrictInput) = launch { repository.addDistrict(input) }
    fun deleteDistrict(districtCode: String) = launch { repository.deleteDistrict(districtCode) }
    fun updateHeroImage(heroId: Int, path: String) = launch { repository.updateHeroImage(heroId, path) }
    fun deleteHero(heroId: Int) = launch { repository.deleteHero(heroId) }
    fun addMemorial(item: Memorial) = launch { repository.addMemorial(item) }
    fun deleteMemorial(id: Int) = launch { repository.deleteMemorial(id) }

    private fun launch(block: suspend () -> Unit) = kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch { block() }
    private fun <T> Flow<T>.stateInVm(initial: T) = stateIn(kotlinx.coroutines.CoroutineScope(Dispatchers.Main), SharingStarted.WhileSubscribed(5000), initial)
}

class MainViewModelFactory(private val repository: NammaKatheyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = MainViewModel(repository) as T
}

@Composable
fun NammaKatheyRoot() {
    val app = LocalContext.current.applicationContext as NammaKatheyApp
    val vm: MainViewModel = viewModel(factory = MainViewModelFactory(app.repository))
    LaunchedEffect(Unit) { vm.initialize() }
    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(primary = Orange, secondary = Blue, tertiary = Green, background = Cream)
    ) {
        Surface(Modifier.fillMaxSize(), color = Cream) {
            val nav = rememberNavController()
            NavHost(navController = nav, startDestination = "splash") {
                composable("splash") { SplashScreen(nav) }
                composable("language") { LanguageScreen(vm, nav) }
                composable("home") { HomeScreen(vm, nav) }
                composable("badges") { BadgesScreen(vm, nav) }
                composable("bookmarks") { BookmarksScreen(vm, nav) }
                composable("statues") { StatueFinderScreen(vm, nav, null) }
                composable("heroes/{district}", listOf(navArgument("district") { type = NavType.StringType })) { HeroesScreen(vm, nav, it.arguments!!.getString("district")!!) }
                composable("story/{heroId}", listOf(navArgument("heroId") { type = NavType.IntType })) { StoryScreen(vm, nav, it.arguments!!.getInt("heroId")) }
                composable("quiz/{heroId}", listOf(navArgument("heroId") { type = NavType.IntType })) { QuizScreen(vm, nav, it.arguments!!.getInt("heroId")) }
                composable("statues/{heroId}", listOf(navArgument("heroId") { type = NavType.IntType })) { StatueFinderScreen(vm, nav, it.arguments!!.getInt("heroId")) }
            }
        }
    }
}

@Composable
fun SplashScreen(nav: NavController) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(900)
        nav.navigate("language") { popUpTo("splash") { inclusive = true } }
    }
    Box(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Cream, Color(0xFFFFE082)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.MilitaryTech, null, tint = Orange, modifier = Modifier.size(108.dp))
            Text("ನಮ್ಮ ಕಥೆ", fontSize = 38.sp, fontWeight = FontWeight.Bold, color = Orange)
            Text("Namma Kathey", fontSize = 22.sp, color = Ink)
            Text("Discover Local Heroes", color = Color.DarkGray)
        }
    }
}

@Composable
fun LanguageScreen(vm: MainViewModel, nav: NavController) {
    Box(Modifier.fillMaxSize().background(Cream).padding(24.dp), contentAlignment = Alignment.Center) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Language, null, tint = Blue, modifier = Modifier.size(88.dp))
            Text("Choose Your Language", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Orange)
            LanguageCard("ಕನ್ನಡ", "Kannada stories and quizzes", Orange) { vm.language.value = "kn"; nav.navigate("home") { popUpTo("language") { inclusive = true } } }
            LanguageCard("English", "English stories and quizzes", Blue) { vm.language.value = "en"; nav.navigate("home") { popUpTo("language") { inclusive = true } } }
        }
    }
}

@Composable
fun LanguageCard(title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(6.dp), shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.padding(22.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(52.dp).clip(CircleShape).background(color.copy(alpha = .15f)), contentAlignment = Alignment.Center) {
                Text(title.take(1), color = color, fontWeight = FontWeight.Bold, fontSize = 26.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
                Text(subtitle, color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(title: String, nav: NavController? = null, actions: @Composable () -> Unit = {}, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { if (nav != null) IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                actions = { actions() },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Orange)
            )
        },
        containerColor = Cream,
        content = content
    )
}

@Composable
fun HomeScreen(vm: MainViewModel, nav: NavController) {
    val districts by vm.districts.collectAsState()
    val lang by vm.language.collectAsState()
    var showAddDistrict by remember { mutableStateOf(false) }
    var districtToDelete by remember { mutableStateOf<District?>(null) }
    AppScaffold(
        title = if (lang == "kn") "ನಮ್ಮ ಕಥೆ" else "Namma Kathey",
        actions = {
            IconButton(onClick = vm::toggleLanguage) { Icon(Icons.Default.Language, null, tint = Color.White) }
            IconButton(onClick = { nav.navigate("badges") }) { Icon(Icons.Default.MilitaryTech, null, tint = Color.White) }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(if (lang == "kn") "ನಿಮ್ಮ ಜಿಲ್ಲೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ" else "Select Your District", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Orange)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { nav.navigate("bookmarks") }) { Icon(Icons.Default.Bookmark, null); Spacer(Modifier.width(6.dp)); Text("Bookmarks") }
                OutlinedButton(onClick = { nav.navigate("statues") }) { Icon(Icons.Default.Map, null); Spacer(Modifier.width(6.dp)); Text("Statues") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { showAddDistrict = true }, colors = ButtonDefaults.buttonColors(Blue)) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (lang == "kn") "ಜಿಲ್ಲೆ ಸೇರಿಸಿ" else "Add District")
                }
            }
            OutlinedTextField(
                value = vm.search.collectAsState().value,
                onValueChange = { vm.search.value = it },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                label = { Text(if (lang == "kn") "ಜಿಲ್ಲೆ ಹುಡುಕಿ" else "Search district") },
                modifier = Modifier.fillMaxWidth()
            )
            LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(districts) { district ->
                    DistrictCard(
                        district = district,
                        lang = lang,
                        onClick = { nav.navigate("heroes/${district.districtCode}") },
                        onLongClick = { districtToDelete = district }
                    )
                }
            }
        }
        FloatingActionButton(onClick = { showAddDistrict = true }, containerColor = Blue, modifier = Modifier.padding(24.dp)) { Icon(Icons.Default.Add, null, tint = Color.White) }
    }
    if (showAddDistrict) AddDistrictDialog(vm, onDismiss = { showAddDistrict = false })
    districtToDelete?.let { district ->
        AlertDialog(
            onDismissRequest = { districtToDelete = null },
            title = { Text("Delete District") },
            text = {
                Text("Delete ${if (lang == "kn") district.districtNameKannada else district.districtNameEnglish}? This will also remove heroes, stories, quizzes, badges, and memorials connected to this district.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.deleteDistrict(district.districtCode)
                        districtToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { districtToDelete = null }) { Text("Cancel") } }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DistrictCard(district: District, lang: String, onClick: () -> Unit, onLongClick: () -> Unit) {
    Card(
        Modifier
            .height(158.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.White, Color(0xFFFFF0C2)))).padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(Icons.Default.LocationOn, null, tint = Orange, modifier = Modifier.size(42.dp))
            Text(if (lang == "kn") district.districtNameKannada else district.districtNameEnglish, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Ink, maxLines = 2)
            Text("${district.heroCount} heroes", color = Color.Gray)
        }
    }
}

@Composable
fun HeroesScreen(vm: MainViewModel, nav: NavController, districtCode: String) {
    val heroesFlow = remember(districtCode) { vm.heroes(districtCode) }
    val heroes by heroesFlow.collectAsState()
    val lang by vm.language.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    AppScaffold(title = districtCode, nav = nav, actions = { IconButton(onClick = { showAdd = true }) { Icon(Icons.Default.Add, null, tint = Color.White) } }) { padding ->
        Column(Modifier.padding(padding)) {
            LazyRow(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("ALL", "FREEDOM_FIGHTER", "POET", "SOCIAL_REFORMER")) { cat ->
                    FilterChip(selected = vm.category.collectAsState().value == cat, onClick = { vm.category.value = cat }, label = { Text(cat.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }) })
                }
            }
            LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(heroes) { hero -> HeroCard(hero, lang, vm, nav) }
            }
        }
    }
    if (showAdd) AddHeroDialog(vm, districtCode, onDismiss = { showAdd = false })
}

@Composable
fun HeroCard(hero: Hero, lang: String, vm: MainViewModel, nav: NavController) {
    Card(shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(5.dp), colors = CardDefaults.cardColors(Color.White)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HeroImage(hero.heroImage, hero.heroNameEnglish, Modifier.size(74.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(if (lang == "kn") hero.heroNameKannada else hero.heroNameEnglish, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Orange)
                    Text(listOfNotNull(hero.birthYear, hero.deathYear).joinToString(" - "), color = Color.Gray)
                    AssistChip(onClick = {}, label = { Text(hero.category.replace('_', ' ')) })
                }
                IconButton(onClick = { vm.toggleBookmark(hero.id) }) { Icon(if (hero.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, null, tint = Orange) }
            }
            Text(if (lang == "kn") hero.shortDescriptionKannada else hero.shortDescriptionEnglish, color = Ink, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { nav.navigate("story/${hero.id}") }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(Orange)) { Icon(Icons.Default.Book, null); Spacer(Modifier.width(4.dp)); Text("Story") }
                Button(onClick = { nav.navigate("quiz/${hero.id}") }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(Blue)) { Icon(Icons.Default.Quiz, null); Spacer(Modifier.width(4.dp)); Text("Quiz") }
            }
            if (hero.isUserCreated) {
                OutlinedButton(onClick = { vm.deleteHero(hero.id) }, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) { Icon(Icons.Default.Delete, null); Spacer(Modifier.width(6.dp)); Text("Delete added hero") }
            }
        }
    }
}

@Composable
fun StoryScreen(vm: MainViewModel, nav: NavController, heroId: Int) {
    val heroFlow = remember(heroId) { vm.hero(heroId) }
    val storyFlow = remember(heroId) { vm.story(heroId) }
    val hero by heroFlow.collectAsState()
    val pages by storyFlow.collectAsState()
    val lang by vm.language.collectAsState()
    val context = LocalContext.current
    var page by remember { mutableIntStateOf(0) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var speaking by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val path = compressImageToPrivateFile(context, uri, "hero_$heroId")
            if (path != null) vm.updateHeroImage(heroId, path)
        }
    }
    DisposableEffect(lang) {
        val engine = TextToSpeech(context) { }
        engine.language = if (lang == "kn") Locale("kn", "IN") else Locale.ENGLISH
        tts = engine
        onDispose { engine.stop(); engine.shutdown() }
    }
    AppScaffold(
        title = hero?.let { if (lang == "kn") it.heroNameKannada else it.heroNameEnglish } ?: "Story",
        nav = nav,
        actions = {
            IconButton(onClick = { launcher.launch("image/*") }) { Icon(Icons.Default.Image, null, tint = Color.White) }
            IconButton(onClick = { vm.toggleBookmark(heroId) }) { Icon(if (hero?.isBookmarked == true) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, null, tint = Color.White) }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (pages.isNotEmpty()) {
                val current = pages[page.coerceIn(0, pages.lastIndex)]
                Column(Modifier.weight(1f).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    HeroImage(hero?.heroImage.orEmpty(), hero?.heroNameEnglish.orEmpty(), Modifier.fillMaxWidth().height(260.dp))
                    Spacer(Modifier.height(18.dp))
                    Text(if (lang == "kn") current.contentKannada else current.contentEnglish, fontSize = 20.sp, lineHeight = 30.sp, color = Ink)
                }
                LinearProgressIndicator(progress = { (page + 1) / pages.size.toFloat() }, modifier = Modifier.fillMaxWidth(), color = Orange)
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(enabled = page > 0, onClick = { page-- }) { Text("Previous") }
                    IconButton(onClick = {
                        val text = if (lang == "kn") current.contentKannada else current.contentEnglish
                        if (speaking) { tts?.stop(); speaking = false } else { tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "story_$page"); speaking = true }
                    }) { Icon(if (speaking) Icons.Default.Stop else Icons.Default.PlayArrow, null, tint = Blue) }
                    Text("Page ${page + 1} of ${pages.size}", Modifier.weight(1f), textAlign = TextAlign.Center)
                    if (page == pages.lastIndex) Button(onClick = { nav.navigate("quiz/$heroId") }, colors = ButtonDefaults.buttonColors(Blue)) { Text("Quiz") } else OutlinedButton(onClick = { page++ }) { Text("Next") }
                }
            }
        }
    }
}

@Composable
fun QuizScreen(vm: MainViewModel, nav: NavController, heroId: Int) {
    val quizFlow = remember(heroId) { vm.quiz(heroId) }
    val questions by quizFlow.collectAsState()
    val lang by vm.language.collectAsState()
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var result by remember { mutableStateOf(false) }
    AppScaffold(title = "Heritage Quiz", nav = nav) { padding ->
        if (questions.isNotEmpty()) {
            val q = questions[index.coerceIn(0, questions.lastIndex)]
            Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Question ${index + 1} of ${questions.size}", color = Blue, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(progress = { (index + 1) / questions.size.toFloat() }, modifier = Modifier.fillMaxWidth(), color = Blue)
                Card(colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(5.dp)) {
                    Text(if (lang == "kn") q.questionKannada else q.questionEnglish, Modifier.padding(22.dp), fontSize = 21.sp, textAlign = TextAlign.Center)
                }
                q.options(lang).forEachIndexed { optionIndex, text ->
                    OutlinedButton(onClick = { selected = optionIndex + 1 }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected == optionIndex + 1) Color(0xFFE3F2FD) else Color.White)) {
                        Text(text, Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                    }
                }
                Button(enabled = selected != 0, onClick = {
                    if (selected == q.correctAnswer) score++
                    selected = 0
                    if (index == questions.lastIndex) {
                        vm.saveBadge(heroId, score)
                        result = true
                    } else index++
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Blue)) { Text(if (index == questions.lastIndex) "Finish Quiz" else "Submit Answer") }
            }
        }
    }
    if (result) AlertDialog(
        onDismissRequest = { result = false },
        icon = { Icon(Icons.Default.MilitaryTech, null, tint = Yellow, modifier = Modifier.size(72.dp)) },
        title = { Text("Heritage Badge Earned!") },
        text = { Text("Score: $score/${questions.size}. Your badge has been saved.") },
        confirmButton = { Button(onClick = { nav.navigate("badges") }) { Text("View Badges") } },
        dismissButton = { TextButton(onClick = { nav.navigate("statues/$heroId") }) { Text("Find Statue") } }
    )
}

fun QuizQuestion.options(lang: String) = if (lang == "kn") listOf(option1Kannada, option2Kannada, option3Kannada, option4Kannada) else listOf(option1English, option2English, option3English, option4English)

@Composable
fun BadgesScreen(vm: MainViewModel, nav: NavController) {
    val badges by vm.badges.collectAsState()
    val lang by vm.language.collectAsState()
    AppScaffold(title = "My Heritage Badges", nav = nav) { padding ->
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(padding), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(Color(0xFFFFF0C2))) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MilitaryTech, null, tint = Yellow, modifier = Modifier.size(64.dp))
                        Text("${badges.size} Badges", fontWeight = FontWeight.Bold, color = Orange)
                    }
                }
            }
            items(badges) { badge ->
                Card(colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MilitaryTech, null, tint = Yellow, modifier = Modifier.size(58.dp))
                        Text(if (lang == "kn") badge.heroNameKannada else badge.heroNameEnglish, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text("${badge.score}/3", color = Green)
                    }
                }
            }
        }
    }
}

@Composable
fun BookmarksScreen(vm: MainViewModel, nav: NavController) {
    val heroes by vm.bookmarks.collectAsState()
    val lang by vm.language.collectAsState()
    AppScaffold(title = "Bookmarks", nav = nav) { padding ->
        LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(heroes) { hero -> HeroCard(hero, lang, vm, nav) }
        }
    }
}

@Composable
fun StatueFinderScreen(vm: MainViewModel, nav: NavController, heroId: Int?) {
    val context = LocalContext.current
    val all by vm.memorials.collectAsState()
    val heroMemorialsFlow = remember(heroId) { heroId?.let { vm.memorials(it) } }
    val selectedHeroMemorials by if (heroMemorialsFlow != null) {
        heroMemorialsFlow.collectAsState()
    } else {
        remember { mutableStateOf(emptyList()) }
    }
    val heroMemorials = if (heroId == null) all else selectedHeroMemorials
    val lang by vm.language.collectAsState()
    var add by remember { mutableStateOf(false) }
    AppScaffold(title = "Statue Finder", nav = nav, actions = { IconButton(onClick = { add = true }) { Icon(Icons.Default.Add, null, tint = Color.White) } }) { padding ->
        LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(Color(0xFFE3F2FD))) {
                    Text("Open directions to memorials and add missing local statues with photos from gallery.", Modifier.padding(16.dp), color = Ink)
                }
            }
            items(heroMemorials) { item ->
                MemorialCard(item, lang, onDirections = { openDirections(context, item) }, onDelete = { vm.deleteMemorial(item.id) })
            }
        }
    }
    if (add) AddMemorialDialog(vm, heroId ?: 1) { add = false }
}

@Composable
fun MemorialCard(item: Memorial, lang: String, onDirections: () -> Unit, onDelete: () -> Unit) {
    Card(colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HeroImage(item.memorialImage, item.memorialNameEnglish, Modifier.size(70.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(if (lang == "kn") item.memorialNameKannada else item.memorialNameEnglish, color = Orange, fontWeight = FontWeight.Bold)
                    Text(if (lang == "kn") item.addressKannada else item.addressEnglish, color = Color.Gray)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onDirections, colors = ButtonDefaults.buttonColors(Blue)) { Icon(Icons.Default.Directions, null); Spacer(Modifier.width(6.dp)); Text("Directions") }
                if (item.isUserCreated) OutlinedButton(onClick = onDelete, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) { Icon(Icons.Default.Delete, null); Text("Delete") }
            }
        }
    }
}

@Composable
fun AddHeroDialog(vm: MainViewModel, districtCode: String?, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val districts by vm.districts.collectAsState()
    var selectedDistrict by remember { mutableStateOf(districtCode ?: "BLG") }
    var name by remember { mutableStateOf("") }
    var nameKn by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("FREEDOM_FIGHTER") }
    var birth by remember { mutableStateOf("") }
    var death by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var story by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if (uri != null) image = compressImageToPrivateFile(context, uri, "hero_custom").orEmpty() }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Local Hero") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(name, { name = it }, label = { Text("English name") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(nameKn, { nameKn = it }, label = { Text("Kannada name") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(birth, { birth = it }, label = { Text("Birth year") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(death, { death = it }, label = { Text("Death year optional") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(desc, { desc = it }, label = { Text("Short description") }, modifier = Modifier.fillMaxWidth(), minLines = 2) }
                item { OutlinedTextField(story, { story = it }, label = { Text("Story pages, separate with |") }, modifier = Modifier.fillMaxWidth(), minLines = 3) }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("FREEDOM_FIGHTER", "POET", "SOCIAL_REFORMER")) {
                            FilterChip(selected = category == it, onClick = { category = it }, label = { Text(it.replace('_', ' ')) })
                        }
                    }
                }
                if (districtCode == null) item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(districts) { FilterChip(selected = selectedDistrict == it.districtCode, onClick = { selectedDistrict = it.districtCode }, label = { Text(it.districtCode) }) }
                    }
                }
                item { OutlinedButton(onClick = { launcher.launch("image/*") }) { Icon(Icons.Default.Image, null); Spacer(Modifier.width(6.dp)); Text(if (image.isBlank()) "Add image from gallery" else "Replace compressed image") } }
            }
        },
        confirmButton = {
            Button(enabled = name.isNotBlank() && desc.isNotBlank(), onClick = {
                vm.addHero(HeroInput(selectedDistrict, name, nameKn, category, birth, death, desc, desc, story.split('|').map { it.trim() }.filter { it.isNotBlank() }.ifEmpty { listOf(desc) }, image))
                onDismiss()
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddDistrictDialog(vm: MainViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var nameKn by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add District") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(name, { name = it }, label = { Text("District name in English") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(nameKn, { nameKn = it }, label = { Text("District name in Kannada") }, modifier = Modifier.fillMaxWidth()) }
            }
        },
        confirmButton = {
            Button(
                enabled = name.isNotBlank() && nameKn.isNotBlank(),
                onClick = {
                    vm.addDistrict(DistrictInput(name, nameKn, "", "", "", "0"))
                    onDismiss()
                }
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddMemorialDialog(vm: MainViewModel, heroId: Int, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("15.8497") }
    var lng by remember { mutableStateOf("74.4977") }
    var image by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if (uri != null) image = compressImageToPrivateFile(context, uri, "memorial_custom").orEmpty() }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Memorial") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Memorial name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(address, { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(lat, { lat = it }, label = { Text("Latitude") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(lng, { lng = it }, label = { Text("Longitude") }, modifier = Modifier.fillMaxWidth())
                OutlinedButton(onClick = { launcher.launch("image/*") }) { Icon(Icons.Default.Image, null); Spacer(Modifier.width(6.dp)); Text(if (image.isBlank()) "Add image from gallery" else "Replace compressed image") }
            }
        },
        confirmButton = {
            Button(enabled = name.isNotBlank(), onClick = {
                vm.addMemorial(Memorial(heroId = heroId, memorialNameEnglish = name, memorialNameKannada = name, addressEnglish = address, addressKannada = address, latitude = lat.toDoubleOrNull() ?: 0.0, longitude = lng.toDoubleOrNull() ?: 0.0, memorialImage = image))
                onDismiss()
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun HeroImage(path: String, fallback: String, modifier: Modifier) {
    val context = LocalContext.current
    val bitmap = remember(path) { loadBitmap(context, path) }
    Box(modifier.clip(RoundedCornerShape(12.dp)).background(Brush.linearGradient(listOf(Orange.copy(alpha = .28f), Blue.copy(alpha = .22f)))), contentAlignment = Alignment.Center) {
        if (bitmap != null) {
            Image(bitmap.asImageBitmap(), contentDescription = fallback, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Text(fallback.split(' ').mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""), color = Orange, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

fun loadBitmap(context: Context, path: String): Bitmap? = try {
    when {
        path.isBlank() -> null
        File(path).exists() -> BitmapFactory.decodeFile(path)
        else -> context.assets.open(path).use { BitmapFactory.decodeStream(it) }
    }
} catch (_: Exception) { null }

fun compressImageToPrivateFile(context: Context, uri: Uri, prefix: String): String? = try {
    val original = context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it) } ?: return null
    val max = 1200f
    val scale = minOf(1f, max / maxOf(original.width, original.height).toFloat())
    val bitmap = if (scale < 1f) Bitmap.createScaledBitmap(original, (original.width * scale).roundToInt(), (original.height * scale).roundToInt(), true) else original
    val dir = File(context.filesDir, "images").apply { mkdirs() }
    val file = File(dir, "${prefix}_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 78, it) }
    file.absolutePath
} catch (_: Exception) { null }

fun openDirections(context: Context, memorial: Memorial) {
    val uri = Uri.parse("google.navigation:q=${memorial.latitude},${memorial.longitude}")
    val intent = Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps")
    if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
    else context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:${memorial.latitude},${memorial.longitude}?q=${memorial.latitude},${memorial.longitude}(${Uri.encode(memorial.memorialNameEnglish)})")))
}

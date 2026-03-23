# MVVM 框架

Android MVVM 快速开发框架，基于 Kotlin + Jetpack + Hilt + Coroutines 构建。

## 📚 技术栈

### 核心框架
| 技术 | 版本 | 说明 |
|------|------|------|
| Kotlin | 1.9.24 | 开发语言 |
| Android Gradle Plugin | 7.4.2 | 构建工具 |
| compileSdk | 34 | 编译 SDK |
| minSdk | 24 | 最低支持 |

### 架构组件
| 技术 | 版本 | 说明 |
|------|------|------|
| Jetpack | | Google 推荐架构 |
| ViewModel | 2.7.0 | 生命周期管理 |
| LiveData | 2.7.0 | 响应式数据 |
| Room | 2.6.1 | 本地数据库 |
| Navigation | 2.7.6 | 页面导航 |

### 网络 & 图片
| 技术 | 版本 | 说明 |
|------|------|------|
| Retrofit | 2.9.0 | RESTful API |
| OkHttp | 4.12.0 | HTTP 客户端 |
| Coil | 2.5.0 | 图片加载 |
| Gson | 2.10.1 | JSON 解析 |

### 依赖注入 & 协程
| 技术 | 版本 | 说明 |
|------|------|------|
| Hilt | 2.48.1 | 依赖注入 |
| Kotlin Coroutines | 1.7.3 | 异步编程 |

---

## 📥 安装

### 1. 添加 JitPack 仓库

在项目根目录的 `build.gradle` 中添加：

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}
```

### 2. 添加框架依赖

在模块的 `build.gradle` 中添加：

```kotlin
dependencies {
    implementation("com.github.sariel20:mvvmframe:v1.0.0")
}
```

---

## 🏗️ 目录结构

```
mvvmframe/
├── src/main/
│   ├── java/com/lc/mvvmframe/
│   │   ├── MvvmFrameApp.kt              # 框架 Application
│   │   │
│   │   ├── data/                         # 数据层
│   │   │   ├── local/                    # 本地数据
│   │   │   │   ├── db/                   # Room 数据库
│   │   │   │   └── sp/                   # SharedPreferences
│   │   │   ├── remote/                   # 远程数据
│   │   │   │   └── api/                  # Retrofit API
│   │   │   └── repository/               # Repository 实现
│   │   │
│   │   ├── di/                           # 依赖注入模块
│   │   │   ├── NetworkModule.kt
│   │   │   ├── DatabaseModule.kt
│   │   │   └── RepositoryModule.kt
│   │   │
│   │   ├── domain/                       # 领域层
│   │   │   ├── model/                    # 数据模型
│   │   │   └── repository/               # Repository 接口
│   │   │
│   │   ├── ui/                           # UI 层
│   │   │   ├── base/                     # 基类
│   │   │   │   ├── IBaseView.kt
│   │   │   │   ├── BaseViewModel.kt
│   │   │   │   ├── BaseActivity.kt
│   │   │   │   └── BaseFragment.kt
│   │   │   └── common/                   # 通用组件
│   │   │       └── LoadingDialog.kt
│   │   │
│   │   └── util/                         # 工具类
│   │       ├── Extensions.kt
│   │       ├── ToastUtils.kt
│   │       ├── TimeUtils.kt
│   │       └── NetworkUtils.kt
│   │
│   └── res/                              # 资源文件
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚀 快速开始

### 1. 配置 Application

```kotlin
@HiltAndroidApp
class App : MvvmFrameApp() {
    override fun onCreate() {
        super.onCreate()
    }
}
```

### 2. 创建 Activity

```kotlin
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override fun createBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): MainViewModel {
        return hiltViewModel()
    }

    override fun initView() {
        setLoadingViewRef(binding.layoutLoading)
        setEmptyViewRef(binding.layoutEmpty)
        bindContentView(binding.layoutContent)
    }

    override fun initData() {
        viewModel.getUserInfo()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModel.user.observe(this) { user ->
            // 处理用户数据
        }
    }
}
```

### 3. 创建 ViewModel

```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel<IBaseView>() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    fun getUserInfo() {
        viewModelScope.launch {
            showLoading()
            when (val result = userRepository.getUserInfo()) {
                is Resource.Success -> {
                    _user.value = result.data
                }
                is Resource.Error -> {
                    showError(result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }
}
```

---

## 📖 核心特性

### 1. BaseViewModel
- 加载状态管理 (`loading`, `error`, `empty`)
- 协程异常自动处理
- `launch()` 快捷方法启动协程
- `handleResult()` 处理网络结果

### 2. BaseActivity / BaseFragment
- ViewBinding 自动绑定
- 加载中/空状态/错误状态 UI 管理
- ViewModel 生命周期关联

### 3. Repository 模式
- 网络请求与本地缓存统一管理
- Flow 响应式数据流
- 自动 Token 注入

### 4. 依赖注入
- Hilt 管理所有依赖
- 模块化配置 (Network, Database, Repository)

---

## 🛠️ 工具类

| 工具类 | 功能 |
|--------|------|
| `ToastUtils` | Toast 显示（全局单例） |
| `TimeUtils` | 时间格式化、相对时间 |
| `NetworkUtils` | 网络状态检测 |
| `Extensions` | View 可见性、图片加载 |

---

## 📡 API 接口文档示例

### 1. 定义 API 接口

```kotlin
interface UserApi {

    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseResponse<User>

    @GET("user/info")
    suspend fun getUserInfo(): BaseResponse<User>

    @POST("user/update")
    suspend fun updateUser(@Body user: User): BaseResponse<User>

    @GET("article/list")
    suspend fun getArticleList(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): BaseResponse<PageData<Article>>
}
```

### 2. 定义数据模型

```kotlin
// 用户实体 - 同时作为 Room 实体和 JSON 模型
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @SerializedName("id")
    val id: Long = 0,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("nickname")
    val nickname: String? = null,

    @SerializedName("avatar")
    val avatar: String? = null,

    @SerializedName("phone")
    val phone: String? = null
) {
    // 计算属性
    val displayName: String
        get() = nickname ?: username ?: "未设置"
}

// 文章实体
@Entity(tableName = "articles")
data class Article(
    @PrimaryKey
    @SerializedName("id")
    val id: Long = 0,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("content")
    val content: String? = null,

    @SerializedName("author")
    val author: String? = null,

    @SerializedName("coverImage")
    val coverImage: String? = null,

    @SerializedName("publishTime")
    val publishTime: Long = 0,

    @SerializedName("viewCount")
    val viewCount: Int = 0
)

// 分页数据封装
data class PageData<T>(
    @SerializedName("list")
    val list: List<T>? = null,

    @SerializedName("total")
    val total: Int = 0,

    @SerializedName("page")
    val page: Int = 1,

    @SerializedName("pageSize")
    val pageSize: Int = 20
) {
    val hasMore: Boolean
        get() = (list?.size ?: 0) >= pageSize
}
```

### 3. 定义 Repository 接口

```kotlin
interface UserRepository {
    suspend fun login(username: String, password: String): Resource<User>
    suspend fun getUserInfo(): Resource<User>
    suspend fun refreshUserInfo(): Resource<User>
    suspend fun updateUser(user: User): Resource<User>
    suspend fun logout()
    fun observeUser(): Flow<User?>
    fun isLoggedIn(): Boolean
}

interface ArticleRepository {
    suspend fun getArticleList(page: Int, pageSize: Int): Resource<PageData<Article>>
    suspend fun getArticleDetail(id: Long): Resource<Article>
    suspend fun refreshArticleList(page: Int, pageSize: Int): Resource<PageData<Article>>
}
```

---

## 💻 详细使用示例

### 1. Fragment 使用示例

```kotlin
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        // 设置状态视图
        setLoadingViewRef(binding.layoutLoading)
        setEmptyViewRef(binding.layoutEmpty)
        bindContentView(binding.layoutContent)

        // 下拉刷新
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    override fun initData() {
        viewModel.loadData()
    }

    override fun observeViewModel() {
        super.observeViewModel()

        // 观察数据列表
        viewModel.articleList.observe(viewLifecycleOwner) { list ->
            binding.swipeRefresh.isRefreshing = false
            if (list.isNullOrEmpty()) {
                showEmpty("暂无数据")
            }
        }

        // 观察加载状态
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }
}
```

### 2. ViewModel 完整示例

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : BaseViewModel<IBaseView>() {

    // 数据列表
    private val _articleList = MutableLiveData<List<Article>>()
    val articleList: LiveData<List<Article>> = _articleList

    // 当前页码
    private var currentPage = 1
    private val pageSize = 20

    init {
        loadData()
    }

    /**
     * 加载数据（首次加载）
     */
    fun loadData() {
        viewModelScope.launch {
            showLoading()
            when (val result = articleRepository.getArticleList(currentPage, pageSize)) {
                is Resource.Success -> {
                    _articleList.value = result.data.list ?: emptyList()
                    hideLoading()
                }
                is Resource.Error -> {
                    showError(result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    /**
     * 刷新数据
     */
    fun refreshData() {
        currentPage = 1
        viewModelScope.launch {
            when (val result = articleRepository.refreshArticleList(currentPage, pageSize)) {
                is Resource.Success -> {
                    _articleList.value = result.data.list ?: emptyList()
                }
                is Resource.Error -> {
                    showError(result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    /**
     * 加载更多
     */
    fun loadMore() {
        viewModelScope.launch {
            currentPage++
            when (val result = articleRepository.getArticleList(currentPage, pageSize)) {
                is Resource.Success -> {
                    val currentList = _articleList.value.orEmpty()
                    _articleList.value = currentList + (result.data.list ?: emptyList())
                }
                is Resource.Error -> {
                    currentPage-- // 加载失败，页码回退
                    showError(result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    /**
     * 使用 launch 快捷方法
     */
    fun deleteArticle(article: Article) {
        launch {
            try {
                // 删除逻辑
                val currentList = _articleList.value.orEmpty().toMutableList()
                currentList.remove(article)
                _articleList.value = currentList
                ToastUtils.show("删除成功")
            } catch (e: Exception) {
                showError(e.message ?: "删除失败")
            }
        }
    }
}
```

### 3. Repository 实现示例

```kotlin
@Singleton
class ArticleRepositoryImpl @Inject constructor(
    private val articleApi: ArticleApi,
    private val articleDao: ArticleDao,
    private val preferencesManager: PreferencesManager
) : ArticleRepository {

    override suspend fun getArticleList(page: Int, pageSize: Int): Resource<PageData<Article>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = articleApi.getArticleList(page, pageSize)
                if (response.isSuccess && response.data != null) {
                    // 缓存数据
                    if (page == 1) {
                        articleDao.deleteAll()
                    }
                    response.data.list?.let { articleDao.insertAll(it) }
                    Resource.Success(response.data)
                } else {
                    // 请求失败，尝试返回缓存
                    val cached = articleDao.getArticles((page - 1) * pageSize, pageSize)
                    if (cached.isNotEmpty()) {
                        Resource.Success(PageData(
                            list = cached,
                            total = cached.size,
                            page = page,
                            pageSize = pageSize
                        ))
                    } else {
                        Resource.Error(response.errorMsg)
                    }
                }
            } catch (e: Exception) {
                // 异常处理，返回缓存
                val cached = articleDao.getArticles((page - 1) * pageSize, pageSize)
                if (cached.isNotEmpty()) {
                    Resource.Success(PageData(
                        list = cached,
                        total = cached.size,
                        page = page,
                        pageSize = pageSize
                    ))
                } else {
                    Resource.Error(e.message ?: "获取文章列表失败", e)
                }
            }
        }
    }

    override suspend fun getArticleDetail(id: Long): Resource<Article> {
        return withContext(Dispatchers.IO) {
            try {
                val response = articleApi.getArticleDetail(id)
                if (response.isSuccess && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    // 尝试缓存
                    articleDao.getArticleById(id)?.let {
                        Resource.Success(it)
                    } ?: Resource.Error(response.errorMsg)
                }
            } catch (e: Exception) {
                articleDao.getArticleById(id)?.let {
                    Resource.Success(it)
                } ?: Resource.Error(e.message ?: "获取文章详情失败", e)
            }
        }
    }

    override suspend fun refreshArticleList(page: Int, pageSize: Int): Resource<PageData<Article>> {
        return getArticleList(page, pageSize)
    }
}
```

### 4. Room DAO 示例

```kotlin
@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<Article>)

    @Query("SELECT * FROM articles ORDER BY publishTime DESC LIMIT :limit OFFSET :offset")
    suspend fun getArticles(offset: Int, limit: Int): List<Article>

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticleById(id: Long): Article?

    @Query("SELECT * FROM articles ORDER BY publishTime DESC")
    fun observeArticles(): Flow<List<Article>>

    @Delete
    suspend fun delete(article: Article)

    @Query("DELETE FROM articles")
    suspend fun deleteAll()
}
```

### 5. 依赖注入模块示例

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.example.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenInterceptor: TokenInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindArticleRepository(impl: ArticleRepositoryImpl): ArticleRepository
}
```

---

## 📄 License

MIT License

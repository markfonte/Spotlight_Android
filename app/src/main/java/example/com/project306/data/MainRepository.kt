package example.com.project306.data

class MainRepository {
    private var firebaseService: FirebaseService = FirebaseService.getInstance()

    fun getFirebaseService() : FirebaseService {
        return firebaseService
    }

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: MainRepository? = null

        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: MainRepository().also { instance = it }
                }
    }
}
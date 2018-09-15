package ca.mcgill.science.ctf.tepid.api

import ca.mcgill.science.ctf.tepid.models.*
import kotlinx.coroutines.experimental.Deferred
import okhttp3.ResponseBody
import retrofit2.http.*
import java.io.InputStream

interface ITepid {

    /*
     * -------------------------------------------
     * Sessions
     * -------------------------------------------
     */

    /**
     * Check if the session token is valid
     * If 200 response is returned, the token is still good
     */
    @GET("sessions/{user}/{token}")
    @MinAuthority(NONE)
    fun validateToken(@Path("user") user: String, @Path("token") token: String): Deferred<Session>

    /**
     * Retrieve a new session given the request body
     * The output will contain the token information used to authenticate
     * for all other requests. See [FullSession] for more info.
     */
    @POST("sessions")
    @MinAuthority(NONE)
    fun getSession(@Body body: SessionRequest): Deferred<Session>

    /**
     * Invalidate the supplied session id
     * See [FullSession.id]
     */
    @DELETE("sessions/{id}")
    @MinAuthority(NONE)
    fun removeSession(@Path("id") id: String): Deferred<Void>

    /*
     * -------------------------------------------
     * Users
     *
     * Note that for the context of querying,
     * sam refers to shortUser, longUser, or studentId
     * Due to typings, extension methods will be added for queries
     * by studentId
     * -------------------------------------------
     */

    /**
     * Fetches a user model from the given sam
     *
     * Unsuccessful responses:
     * 404  user could not be found
     * 401  invalid session or unauthorized access to another user
     */
    @GET("users/{sam}")
    @MinAuthority(USER)
    fun getUser(@Path("sam") sam: String): Deferred<User>

    /**
     * Helper for when sam is an id
     *
     * See [getUser]
     */
    @GET("users/{id}?noRedirect")
    @MinAuthority(USER)
    fun getUser(@Path("id") id: Int): Deferred<User>

    /**
     * Get quota of the supplied sam
     * If sender is a user, the sam must also match their own sam
     * If sender is a ctfer, no restriction is applied on the sam
     */
    @GET("users/{sam}/quota")
    @MinAuthority(USER)
    fun getQuota(@Path("sam") sam: String): Deferred<Int>

    /**
     * Returns true if a local admin exists
     *
     */
    @GET("users/configured")
    @MinAuthority(NONE)
    fun isConfigured(): Deferred<Boolean>

    /**
     * Create a new local admin with the given data
     * This is only authorized when no existing local admin exists
     */
    @PUT("users/{sam}")
    @MinAuthority(NONE)
    fun createLocalAdmin(@Path("sam") sam: String, @Body admin: User): Deferred<PutResponse>

    /**
     * Gets a list of short users similar to the one supplied
     *
     * query will be used to match short users or display names
     */
    @GET("users/autosuggest/{expr}")
    @MinAuthority(CTFER)
    fun queryUsers(@Path("expr") query: String, @Query("limit") limit: Int): Deferred<List<UserQuery>>

    /**
     * Sets the exchange student status for the given user
     */
    @PUT("users/{sam}/exchange")
    @MinAuthority(CTFER)
    fun setExchange(@Path("sam") sam: String, @Body enable: Boolean): Deferred<PutResponse>

    /**
     * Sets the color toggle for the given user
     * If sender is a user, the sam must also match their own sam
     * If sender is a ctfer, no restriction is applied on the sam
     */
    @PUT("users/{sam}/color")
    @MinAuthority(USER)
    fun enableColor(@Path("sam") sam: String, @Body enable: Boolean): Deferred<PutResponse>

    /**
     * Sets the nickname for the given user
     * If sender is a user, the sam must also match their own sam
     * If sender is a ctfer, no restriction is applied on the sam
     *
     * A nickname that is blank will effectively set the nickname to null,
     * and the default name will be used
     */
    @PUT("users/{sam}/nick")
    @MinAuthority(USER)
    fun setNickname(@Path("sam") sam: String, @Body nickname: String): Deferred<PutResponse>

    /**
     * Sets the new job expiration for the current user
     * If sender is a user, the sam must also match their own sam
     * If sender is a ctfer, no restriction is applied on the sam
     */
    @PUT("users/{sam}/jobExpiration")
    @MinAuthority(USER)
    fun setJobExpiration(@Path("sam") sam: String, @Body jobExpiration: Long): Deferred<PutResponse>

    /*
     * -------------------------------------------
     * Destinations
     * -------------------------------------------
     */

    /**
     * Retrieve a map of room names to destinations
     */
    @GET("destinations")
    @MinAuthority(USER)
    fun getDestinations(): Deferred<Map<String, Destination>>

    /**
     * Remap all existing destinations
     *
     * //todo verify
     */
    @PUT("destinations")
    @MinAuthority(ELDER)
    fun putDestinations(destinations: Map<String, Destination>): Deferred<PutResponse>

    /**
     * Delete the specified destination
     */
    @DELETE("destinations/{printerId}")
    @MinAuthority(ELDER)
    fun deleteDestination(@Path("printerId") printerId: String): Deferred<String>

    /**
     * Send a ticket to the given printer by id
     */
    @POST("destinations/{printerId}")
    @MinAuthority(CTFER)
    fun setPrinterStatus(@Path("printerId") printerId: String, @Body ticket: DestinationTicket): Deferred<String>

    /*
     * -------------------------------------------
     * Queues
     * -------------------------------------------
     */

    /**
     * Set the full list of queues
     *
     * todo verify
     */
    @PUT("queues")
    @MinAuthority(ELDER)
    fun putQueues(queues: List<PrintQueue>): Deferred<PutResponse>

    /**
     * Get the full list of queues
     */
    @GET("queues")
    @MinAuthority(NONE)
    fun getQueues(): Deferred<List<PrintQueue>>

    @DELETE("queues/{queue}")
    @MinAuthority(ELDER)
    fun deleteQueue(@Path("queue") queue: String): Deferred<String>

    /**
     * Get the list of print jobs for the given queue
     *
     * todo figure out what the queue is. Likely [PrinterId.serialNumber]?
     */
    @GET("queues/{queue}")
    @MinAuthority(NONE)
    fun getPrintJobs(@Path("queue") queue: String, @Query("limit") limit: Int): Deferred<List<PrintJob>>

    /**
     * Gets a single print job from the specified queue and id
     *
     * todo return 404 if not found
     */
    @GET("queues/{queue}/{id}")
    @MinAuthority(NONE)
    fun getPrintJob(@Path("queue") queue: String, @Path("id") id: String): Deferred<PrintJob>

    /**
     * Gets a single file from the specified queue, id, and filename
     *
     * todo this should have at least ctf access?
     */
    @GET("queues/{queue}/{id}/{file}")
    @MinAuthority(NONE)
    fun getAttachment(@Path("queue") queue: String, @Path("id") id: String, @Path("file") file: String): Deferred<ResponseBody>

    /**
     * todo implement
     */
    //@GET("queues/_changes")

    /**
     * Return list of load balancers available
     */
    @GET("queues/loadbalancers")
    @MinAuthority(NONE)
    fun getLoadBalancers(): List<String>

    /*
     * -------------------------------------------
     * Jobs
     * -------------------------------------------
     */

    /**
     * Get the list of print jobs for the given user
     *
     * todo add limit query
     */
    @GET("jobs/{sam}")
    @MinAuthority(USER)
    fun getUserPrintJobs(@Path("sam") sam: String): Deferred<List<PrintJob>>

    /**
     * Create a print job
     *
     * Note that a job's must be one of [Room.toString]
     */
    @POST("jobs")
    @MinAuthority(USER)
    fun createNewJob(@Body job: PrintJob): Deferred<PutResponse>

    /**
     * Add job data to the supplied id
     *
     * todo unify output
     */
    @PUT("jobs/{id}")
    @MinAuthority(USER)
    fun addJobData(@Path("id") id: String, @Body input: InputStream): Deferred<String>

    /**
     * Get the print job with the [PrintJob._id]
     */
    @GET("jobs/job/{id}")
    @MinAuthority(USER)
    fun getJob(@Path("id") id: String): Deferred<PrintJob>

    /**
     * Refund the print job with the supplied [PrintJob._id]
     */
    @PUT("jobs/job/{id}/refunded")
    @MinAuthority(CTFER)
    fun refundJob(@Path("id") id: String, @Body refund: Boolean): Deferred<PutResponse>

    /**
     * Reprint the print job with the supplied id
     */
    @POST("jobs/job/{id}/reprint")
    @MinAuthority(USER)
    fun reprintJob(@Path("id") id: String): Deferred<String>

    /**
     * Get changes for the specified job id
     */
    @GET("jobs/job/{id}/_changes")
    @MinAuthority(USER)
    fun getJobChanges(@Path("id") id: String, @Query("feed") feed: String, @Query("since") since: String): Deferred<List<ChangeDelta>>


    /*
     * -------------------------------------------
     * Misc
     * -------------------------------------------
     */

    /**
     * Retrieve data about the current tepid build
     * Does not require authentication
     */
    @GET("about")
    @MinAuthority(NONE)
    fun getAbout(): Deferred<About>

    /**
     * Retrieves a string representing the authorized endpoints
     */
    @GET("endpoints")
    @MinAuthority(CTFER)
    fun getEndpoints(): Deferred<String>

//    @GET("barcode/_wait")
//    fun scanBarcode(): Deferred<UserBarcode>
}

private const val NONE = "none"

/*
 * -------------------------------------------
 * Extensions
 * -------------------------------------------
 */

/*
 * -------------------------------------------
 * Id extensions
 *
 * Converts ids to strings
 * -------------------------------------------
 */

fun ITepid.setNickname(id: Int, nickname: String) = setNickname(id.toString(), nickname)
fun ITepid.setExchange(id: Int, enable: Boolean) = setExchange(id.toString(), enable)
fun ITepid.enableColor(id: Int, enable: Boolean) = enableColor(id.toString(), enable)
fun ITepid.setJobExpiration(id: Int, jobExpiration: Long) = setJobExpiration(id.toString(), jobExpiration)
fun ITepid.getQuota(id: Int) = getQuota(id.toString())
fun ITepid.getUserPrintJobs(id: Int) = getUserPrintJobs(id.toString())
fun ITepid.refundJob(id: String) = refundJob(id, true)
/*
 * -------------------------------------------
 * Query extensions
 *
 * Supplies defaults to certain queries
 * -------------------------------------------
 */
fun ITepid.queryUsers(query: String) = queryUsers(query, -1)

fun ITepid.getPrintJobs(query: String) = getPrintJobs(query, -1)

fun ITepid.getJobChanges(id: String) = getJobChanges(id, "longpoll", "now")
//fun ITepid.getJobChanges(id: String, since: Long) = getJobChanges(id, "longpoll", since.toString())

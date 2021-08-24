package lnq.com.lnq.api;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lnq.com.lnq.endpoints.EndpointUrls;
import lnq.com.lnq.fragments.chat.GroupChatMainObject;
import lnq.com.lnq.model.attachements.SendAttachementsModel;
import lnq.com.lnq.model.attachements.SendMultipleAttachementsModel;
import lnq.com.lnq.model.attachements.SendVoiceAttachment;
import lnq.com.lnq.model.attachements.SendVoiceModel;
import lnq.com.lnq.model.defaultsetting.DefaultSetting;
import lnq.com.lnq.model.defaultsetting.UserContactUs;
import lnq.com.lnq.model.gson_converter_models.Contacts.RemoveUserFromGroup;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.ExportCSVModel;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.InviteLNQMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserConnectionsMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserContactGroupMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.connections.UserGetGroupMainObject;
import lnq.com.lnq.model.gson_converter_models.Contacts.export_contacts.ExportContactsMainObject;
import lnq.com.lnq.model.gson_converter_models.EditSocialLinksMainObject;
import lnq.com.lnq.model.gson_converter_models.LogOut;
import lnq.com.lnq.model.gson_converter_models.chat.MuteChatMainObject;
import lnq.com.lnq.model.gson_converter_models.activity.ActivityMainObject;
import lnq.com.lnq.model.gson_converter_models.blockedusers.GetBlockedUsersMainObject;
import lnq.com.lnq.model.gson_converter_models.chat.GetChatMainObject;
import lnq.com.lnq.model.gson_converter_models.chat.MuteGroupChatMainObject;
import lnq.com.lnq.model.gson_converter_models.chat.UnMuteChatMainObject;
import lnq.com.lnq.model.gson_converter_models.chat.UnMuteGroupChatMainObject;
import lnq.com.lnq.model.gson_converter_models.conversation.AddGroupMemberMainObject;
import lnq.com.lnq.model.gson_converter_models.conversation.ChatThread;
import lnq.com.lnq.model.gson_converter_models.conversation.CreateGroupNameMainObject;
import lnq.com.lnq.model.gson_converter_models.conversation.CreateGroupThreadMainObject;
import lnq.com.lnq.model.gson_converter_models.conversation.SendGroupMessageMainObject;
import lnq.com.lnq.model.gson_converter_models.conversation.ShareGroupProfileMainObject;
import lnq.com.lnq.model.gson_converter_models.conversation.ShareProfileMainObject;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationMainObject;
import lnq.com.lnq.model.gson_converter_models.location.UserWithOutRadiusMainObject;
import lnq.com.lnq.model.gson_converter_models.profile_information.ChangeUserProfileStatusMainObject;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateMultipleProfileMainObject;
import lnq.com.lnq.model.gson_converter_models.profile_information.CreateUserProfileMainObject;
import lnq.com.lnq.model.gson_converter_models.pushnotifications.PushNotificationMainObject;
import lnq.com.lnq.model.gson_converter_models.qr_code.InviteUserMainObject;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.ForgetPasswordNewModel;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.model.gson_converter_models.search_city_zip.SearchCityZipObject;
import lnq.com.lnq.model.gson_converter_models.searchuser.SearchUser;
import lnq.com.lnq.model.gson_converter_models.send_message.SendMessageMainObject;
import lnq.com.lnq.model.gson_converter_models.tags.UserTagsMainObject;
import lnq.com.lnq.model.gson_converter_models.tasknote.CreateTaskModel;
import lnq.com.lnq.model.gson_converter_models.tasknote.TaskNoteMainObject;
import lnq.com.lnq.model.gson_converter_models.visibilitysettings.SetVisibilityMainObject;
import lnq.com.lnq.model.userprofile.GetUserProfileMainObject;
import lnq.com.lnq.model.userprofile.UserTasks;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    //        String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";
//    String XWWWORMURLENCODED = "application/x-www-form-urlencoded";
    String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            response.header("Content-Type", APPLICATION_JSON_CHARSET_UTF_8);
            response.header("Accept", APPLICATION_JSON_CHARSET_UTF_8);
            return response;
        }
    }).addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).readTimeout(120, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS).retryOnConnectionFailure(true);

    OkHttpClient client = httpClient.build();

    Api WEB_SERVICE = new Retrofit.Builder()
            .baseUrl(EndpointUrls.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build().create(Api.class);

    @FormUrlEncoded
    @POST("signup")
    Call<RegisterLoginMainObject> signup(@Header("x-api-key") String secret_key, @Field("email") String email, @Field("pass") String password);
//    Call<RegisterLoginMainObject> signup(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Field("email") String email, @Field("pass") String password);

    @FormUrlEncoded
    @POST("login")
    Call<RegisterLoginMainObject> login(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("email") String email, @Field("pass") String password);
//    Call<RegisterLoginMainObject> login(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("email") String email, @Field("pass") String password);

    @FormUrlEncoded
    @POST("login")
    Call<RegisterLoginMainObject> loginMagicLink(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("email") String email, @Field("pass") String password, @Field("magic_login") String magic_login);
//    Call<RegisterLoginMainObject> login(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("email") String email, @Field("pass") String password);

    @FormUrlEncoded
    @POST("forgotPasswordNew")
    Call<RegisterLoginMainObject> forgotPassword(@Header("x-api-key") String secret_key, @Field("email") String email, @Field("hyperlink") String hyperlink);
//    Call<RegisterLoginMainObject> forgotPassword(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Field("email") String email);

    @FormUrlEncoded
    @POST("phoneVerification")
    Call<RegisterLoginMainObject> phoneVerification(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("id") String id, @Field("user_fname") String first_name, @Field("user_lname") String last_name, @Field("user_phone") String phone, @Field("verification_status") String verification_status, @Field("user_nickname") String user_nickname);
//    Call<RegisterLoginMainObject> phoneVerification(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("id") String id, @Field("user_fname") String first_name, @Field("user_lname") String last_name, @Field("user_phone") String phone, @Field("verification_status") String verification_status);

    @FormUrlEncoded
    @POST("updateUserProfileV1e")
    Call<CreateUserProfileMainObject> updateUserProfile(
            @Header("x-api-key") String secret_key,
            @Header("Authorization") String basicAuth,
            @Field("id") String id,
            @Field("user_fname") String user_fname,
            @Field("user_lname") String user_lname,
            @Field("user_current_position") String user_current_position,
            @Field("user_company") String user_company,
            @Field("user_address") String user_address,
            @Field("user_birthday") String user_birthday,
            @Field("user_gender") String user_gender,
            @Field("profile_id") String profile_id,
            @Field("user_nickname") String user_nickname
    );

    @FormUrlEncoded
    @POST("createUserSecondaryProfile")
    Call<CreateMultipleProfileMainObject> createUserSecondaryProfile(
            @Header("x-api-key") String secret_key,
            @Header("Authorization") String basicAuth,
            @Field("id") String id,
            @Field("user_fname") String user_fname,
            @Field("user_lname") String user_lname,
            @Field("user_nickname") String user_nickname,
            @Field("user_current_position") String user_current_position,
            @Field("user_company") String user_company,
            @Field("user_address") String user_address,
            @Field("user_birthday") String user_birthday,
            @Field("user_gender") String user_gender
    );

//    Call<CreateUserProfileMainObject> updateUserProfile(
//            @Header("x-api-key") String secret_key,
//            @Header("sent_at") String sent_at,
//            @Header("Authorization") String basicAuth,
//            @Field("id") String id,
//            @Field("user_fname") String user_fname,
//            @Field("user_lname") String user_lname,
//            @Field("user_current_position") String user_current_position,
//            @Field("user_company") String user_company,
//            @Field("user_address") String user_address,
//            @Field("user_birthday") String user_birthday
//    );

    @FormUrlEncoded
    @POST("forgotEmail")
    Call<RegisterLoginMainObject> forgotEmail(@Header("x-api-key") String secret_key, @Field("user_fname") String first_name, @Field("user_lname") String last_name, @Field("user_phone") String phone);
//    Call<RegisterLoginMainObject> forgotEmail(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Field("user_fname") String first_name, @Field("user_lname") String last_name, @Field("user_phone") String phone);

    @FormUrlEncoded
    @POST("reset-passwordNew")
    Call<ForgetPasswordNewModel> forgotPasswordNew(@Header("x-api-key") String secret_key, @Field("pass_token") String pass_token, @Field("user_pass") String user_pass, @Field("conf_user_pass") String conf_user_pass);

    @FormUrlEncoded
    @POST("createUserProfileV1e")
    Call<CreateUserProfileMainObject> createProfile(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("id") String id, @Field("user_address") String user_address, @Field("user_current_position") String user_current_position, @Field("user_company") String user_company, @Field("user_birthday") String user_birthday, @Field("user_bio") String user_bio, @Field("user_gender") String user_gender);
//    Call<CreateUserProfileMainObject> createProfile(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("id") String id, @Field("user_address") String user_address, @Field("user_current_position") String user_current_position, @Field("user_company") String user_company, @Field("user_birthday") String user_birthday, @Field("user_bio") String user_bio);

    @FormUrlEncoded
    @POST("switchActiveUserProfile")
    Call<ChangeUserProfileStatusMainObject> switchActiveUserProfile(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("profile_id") String profile_id);
//    Call<CreateUserProfileMainObject> createProfile(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("id") String id, @Field("user_address") String user_address, @Field("user_current_position") String user_current_position, @Field("user_company") String user_company, @Field("user_birthday") String user_birthday, @Field("user_bio") String user_bio);


    @Multipart
    @POST("uploadProfileImage")
    Call<RegisterLoginMainObject> uploadProfileImage(
            @Header("x-api-key") String secret_key,
            @Header("Authorization") String basicAuth,
            @Part MultipartBody.Part filePart,
            @Part("id") RequestBody id,
            @Part("avatar_from") RequestBody avatar_from,
            @Part("image_type") RequestBody image_type
    );
//    Call<RegisterLoginMainObject> uploadProfileImage(
//            @Header("x-api-key") String secret_key,
//            @Header("sent_at") String sent_at,
//            @Header("Authorization") String basicAuth,
//            @Part MultipartBody.Part filePart,
//            @Part("id") RequestBody id,
//            @Part("avatar_from") RequestBody avatar_from,
//            @Part("image_type") RequestBody image_type
//    );

    @FormUrlEncoded
    @POST("setPushNotifications")
    Call<PushNotificationMainObject> notificationsSet(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("id") String id, @Field("device_type") String device_type, @Field("device_tocken") String device_tocken, @Field("push_notification") String push_notification, @Field("last_login") String last_login);
//    Call<PushNotificationMainObject> notificationsSet(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("id") String id, @Field("device_type") String device_type, @Field("device_tocken") String device_tocken, @Field("push_notification") String push_notification, @Field("last_login") String last_login);

    @GET("searchStates")
    Call<SearchCityZipObject> searchState(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Query("key") String key, @Query("user_id") String user_id);
//    Call<SearchCityZipObject> searchState(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Query("key") String key);

    @FormUrlEncoded
    @POST("logout")
    Call<LogOut> logout(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("device_type") String device_type, @Field("device_tocken") String device_token);
//    Call<LogOut> logout(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("device_type") String device_type, @Field("device_tocken") String device_token);

    @FormUrlEncoded
    @POST("updateStatusMsg")
    Call<CreateUserProfileMainObject> updateStatusMsg(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("id") String id, @Field("status_msg") String status_msg, @Field("profile_id") String profile_id);
//    Call<CreateUserProfileMainObject> updateStatusMsg(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("id") String id, @Field("status_msg") String status_msg);

    @FormUrlEncoded
    @POST("userContacts")
    Call<UserConnectionsMainObject> contacts(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("search_key") String searchKey, @Field("search_filters") String searchFilter, @Field("profile_id") String profile_id);
//    Call<UserConnectionsMainObject> contacts(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("search_key") String searchKey, @Field("search_filters") String searchFilter);

    @FormUrlEncoded
    @POST("createUserGroup")
    Call<UserContactGroupMainObject> createUserGroup(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("profile_id") String profile_id, @Field("group_name") String group_name, @Field("receiver_ids") String receiver_ids, @Field("receiver_profile_ids") String receiver_profile_ids);

    @FormUrlEncoded
    @POST("createUserGroup")
    Call<UserContactGroupMainObject> createUserGroupWithGroupId(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("profile_id") String profile_id, @Field("group_name") String group_name, @Field("receiver_ids") String receiver_ids, @Field("receiver_profile_ids") String receiver_profile_ids, @Field("group_id") String group_id);

    @FormUrlEncoded
    @POST("getUserGroups")
    Call<UserGetGroupMainObject> getUserGroups(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("profile_id") String profile_id);

    @FormUrlEncoded
    @POST("removeMemberFromUserGroup")
    Call<RemoveUserFromGroup> removeMemberFromUserGroup(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("profile_id") String profile_id, @Field("receiver_ids") String receiver_ids, @Field("receiver_profile_ids") String receiver_profile_ids, @Field("group_id") String group_id, @Field("remove_all_member") String remove_all_member);

    @FormUrlEncoded
    @POST("isPhoneUnique")
//    Call<RegisterLoginMainObject> isPhoneUnique(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("user_phone") String user_phone);
    Call<RegisterLoginMainObject> isPhoneUnique(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_phone") String user_phone);

    @FormUrlEncoded
    @POST("importContacts")
    Call<ExportContactsMainObject> exportContacts(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("user_contacts") String user_contacts);
//    Call<ExportContactsMainObject> exportContacts(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("user_contacts") String user_contacts);

    @FormUrlEncoded
    @POST("contactRequest")
    Call<UpdateLocationMainObject> contactRequest(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("sender_user_id") String sender_user_id, @Field("receiver_user_id") String receiver_user_id, @Field("request_from") String request_from, @Field("sender_profile_id") String sender_profile_id, @Field("receiver_profile_id") String receiver_profile_id);
//    Call<UpdateLocationMainObject> contactRequest(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("sender_user_id") String sender_user_id, @Field("receiver_user_id") String receiver_user_id, @Field("request_from") String request_from);

    @FormUrlEncoded
    @POST("contactUs")
    Call<UserContactUs> contactUs(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("subject") String subject, @Field("message") String message);

    @FormUrlEncoded
    @POST("contactRequestCancel")
    Call<UpdateLocationMainObject> contactRequestCancel(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("sender_user_id") String user_id, @Field("receiver_user_id") String req_user_id, @Field("sender_profile_id") String sender_profile_id, @Field("receiver_profile_id") String receiver_profile_id);
//    Call<UpdateLocationMainObject> contactRequestCancel(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("sender_user_id") String user_id, @Field("receiver_user_id") String req_user_id);

    @FormUrlEncoded
    @POST("contactRequestAccpet")
    Call<UpdateLocationMainObject> contactRequestAccpet(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("sender_user_id") String user_id, @Field("receiver_user_id") String req_user_id, @Field("sender_profile_id") String sender_profile_id, @Field("receiver_profile_id") String receiver_profile_id);
//    Call<UpdateLocationMainObject> contactRequestAccpet(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("sender_user_id") String user_id, @Field("receiver_user_id") String req_user_id);

    @FormUrlEncoded
    @POST("unLNQ")
    Call<UpdateLocationMainObject> unLNQ(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("sender_user_id") String user_id, @Field("receiver_user_id") String req_user_id, @Field("sender_profile_id") String sender_profile_id, @Field("receiver_profile_id") String receiver_profile_id);
//    Call<UpdateLocationMainObject> unLNQ(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("sender_user_id") String user_id, @Field("receiver_user_id") String req_user_id);

    @FormUrlEncoded
    @POST("inviteLNQ")
    Call<InviteLNQMainObject> inviteLNQ(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("referral_fname") String referral_fname, @Field("referral_lname") String referral_lname, @Field("referral_email") String referral_email, @Field("referral_phone") String referral_phone);


    @Multipart
    @POST("updateProfileImage")
    Call<CreateUserProfileMainObject> updateProfileImage(
            @Header("x-api-key") String secret_key,
            @Header("Authorization") String basicAuth,
            @Part MultipartBody.Part filePart,
            @Part("user_id") RequestBody id,
            @Part("profile_id") RequestBody profile_id,
            @Part("avatar_from") RequestBody avatar_from
    );


//    Call<CreateUserProfileMainObject> updateProfileImage(
//            @Header("x-api-key") String secret_key,
//            @Header("sent_at") String sent_at,
//            @Header("Authorization") String basicAuth,
//            @Part MultipartBody.Part filePart,
//            @Part("user_id") RequestBody id,
//            @Part("avatar_from") RequestBody avatar_from
//    );

    @FormUrlEncoded
    @POST("userActivity")
    Call<ActivityMainObject> userActivity(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("search_key") String searchKey, @Field("search_filters") String searchFilter, @Field("profile_id") String profile_id);
//    Call<ActivityMainObject> userActivity(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("search_key") String searchKey, @Field("search_filters") String searchFilter);

    @GET("getUserProfile/{loggedin_user_id}/{user_id}")
    Call<GetUserProfileMainObject> getUserProfile(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Path("loggedin_user_id") String loggedIn, @Path("user_id") String userId);
//    Call<GetUserProfileMainObject> getUserProfile(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Path("loggedin_user_id") String loggedIn, @Path("user_id") String userId);

    @GET("getUserActiveProfile/{loggedin_user_id}/{user_id}/{sender_profile_id}/{receiver_profile_id}")
    Call<GetUserProfileMainObject> getUserActiveProfile(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Path("loggedin_user_id") String loggedIn, @Path("user_id") String userId, @Path("sender_profile_id") String senderProfileId, @Path("receiver_profile_id") String receiverProfileId);
//    Call<GetUserProfileMainObject> getUserProfile(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Path("loggedin_user_id") String loggedIn, @Path("user_id") String userId);

    @FormUrlEncoded
    @POST("userInterests")
    Call<UserTagsMainObject> userTags(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("user_interests") String user_interests, @Field("log_activity") String log_activity, @Field("profile_id") String profile_id);
//    Call<UserTagsMainObject> userTags(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("user_tags") String user_tags, @Field("log_activity") String log_activity);

    @FormUrlEncoded
    @POST("updateUserBio")
    Call<CreateUserProfileMainObject> updateUserBio(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("user_bio") String user_bio, @Field("profile_id") String profile_id);
//    Call<CreateUserProfileMainObject> updateUserBio(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("user_bio") String user_bio);

    @FormUrlEncoded
    @POST("editNote")
    Call<TaskNoteMainObject> editNote(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("note_id") String note_id, @Field("note_description") String note_des);
//    Call<TaskNoteMainObject> editNote(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("note_id") String note_id, @Field("note_description") String note_des);

    @FormUrlEncoded
    @POST("createNote")
    Call<TaskNoteMainObject> createNote(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id_note_by") String user_id_note_by, @Field("user_id_note_for") String user_id_note_for, @Field("note_description") String note_des, @Field("profile_id_note_by") String profile_id_note_by, @Field("profile_id_note_for") String profile_id_note_for);
//    Call<TaskNoteMainObject> createNote(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("user_id_note_by") String user_id_note_by, @Field("user_id_note_for") String user_id_note_for, @Field("note_description") String note_des);

    @FormUrlEncoded
    @POST("editTask")
    Call<TaskNoteMainObject> editTask(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("task_id") String task_id, @Field("task_description") String task_des, @Field("task_duedate") String task_duedate);
//    Call<TaskNoteMainObject> editTask(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("task_id") String task_id, @Field("task_description") String task_des);

    @FormUrlEncoded
    @POST("createTaskV1")
    Call<CreateTaskModel> createTaskV1(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id_task_by") String user_id_task_by, @Field("user_id_task_for") String user_id_task_for, @Field("task_description") String task_description, @Field("task_duedate") String task_duedate, @Field("task_status") String task_status, @Field("profile_id_task_by") String profile_id_task_by, @Field("profile_id_task_for") String profile_id_task_for);
//    Call<TaskNoteMainObject> editTask(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("task_id") String task_id, @Field("task_description") String task_des);

    @FormUrlEncoded
    @POST("completeTaskV1")
    Call<UserTasks> completeTaskV1(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("task_id") String task_id);
//    Call<TaskNoteMainObject> editTask(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("task_id") String task_id, @Field("task_description") String task_des);


    @FormUrlEncoded
    @POST("createTask")
    Call<TaskNoteMainObject> createTask(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id_task_by") String user_id_task_by, @Field("user_id_task_for") String user_id_task_for, @Field("task_description") String task_des);
//    Call<TaskNoteMainObject> createTask(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("user_id_task_by") String user_id_task_by, @Field("user_id_task_for") String user_id_task_for, @Field("task_description") String task_des);

    @FormUrlEncoded
    @POST("updateLocation")
    Call<UpdateLocationMainObject> updateLocation(@Header("x-api-key") String secret_key, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("profile_id") String profile_id, @Field("user_lat") String user_lat, @Field("user_long") String user_long, @Field("radius") String radius, @Field("location") String location, @Field("search_key") String searchKey, @Field("search_filters") String searchFilter, @Field("zoom_level") String zoomLevel);
//    Call<UpdateLocationMainObject> updateLocation(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String basicAuth, @Field("user_id") String user_id, @Field("user_lat") String user_lat, @Field("user_long") String user_long, @Field("radius") String radius, @Field("location") String location, @Field("search_key") String searchKey, @Field("search_filters") String searchFilter, @Field("zoom_level") String zoomLevel);

    @FormUrlEncoded
    @POST("usersInRadius")
    Call<UpdateLocationMainObject> usersInRadius(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("profile_id") String profile_id, @Field("center_lat") String user_lat, @Field("center_long") String user_long, @Field("radius") String radius, @Field("search_key") String searchKey, @Field("search_filters") String searchFilter, @Field("zoom_level") String zoomLevel);
//    Call<UpdateLocationMainObject> usersInRadius(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("center_lat") String user_lat, @Field("center_long") String user_long, @Field("radius") String radius, @Field("search_key") String searchKey, @Field("search_filters") String searchFilter, @Field("zoom_level") String zoomLevel);

    @FormUrlEncoded
    @POST("usersWithOutRadius")
    Call<UserWithOutRadiusMainObject> usersWithOutRadius(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("profile_id") String profile_id, @Field("search_key") String search_key);

    @FormUrlEncoded
    @POST("favUnfavLNQ")
    Call<UpdateLocationMainObject> favUnfavLNQ(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("sender_id") String sender_id, @Field("receiver_id") String receiver_id, @Field("connection_status") String connection_status, @Field("sender_profile_id") String sender_profile_id, @Field("receiver_profile_id") String receiver_profile_id);
//    Call<UpdateLocationMainObject> favUnfavLNQ(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("sender_id") String sender_id, @Field("receiver_id") String receiver_id, @Field("connection_status") String connection_status);

    @FormUrlEncoded
    @POST("blockUnblockLNQ")
    Call<RegisterLoginMainObject> blockUnblockLNQ(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("sender_id") String sender_id, @Field("receiver_id") String receiver_id, @Field("status") String status, @Field("sender_profile_id") String sender_profile_id, @Field("receiver_profile_id") String receiver_profilr_id);

    @FormUrlEncoded
    @POST("blockUnblockLNQ")
    Call<RegisterLoginMainObject> blockUnblockLNQOld(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("sender_id") String sender_id, @Field("receiver_id") String receiver_id, @Field("status") String status);
//    Call<RegisterLoginMainObject> blockUnblockLNQ(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("sender_id") String sender_id, @Field("receiver_id") String receiver_id, @Field("status") String status);

    @FormUrlEncoded
    @POST("listBlockedUsers")
    Call<GetBlockedUsersMainObject> getBlockedUsers(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("profile_id") String profile_id);
//    Call<GetBlockedUsersMainObject> getBlockedUsers(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("userFilters")
//    Call<RegisterLoginMainObject> getUserFilters(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("user_filters") String user_filter);
    Call<RegisterLoginMainObject> getUserFilters(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("user_filters") String user_filter);

    @FormUrlEncoded
    @POST("logActivity")
    Call<RegisterLoginMainObject> logActivity(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("source_user") String source_user, @Field("target_user") String target_user, @Field("activity_type") String activity_type);
//    Call<RegisterLoginMainObject> logActivity(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("source_user") String source_user, @Field("target_user") String target_user, @Field("activity_type") String activity_type);

    @FormUrlEncoded
    @POST("setVisibility")
    Call<SetVisibilityMainObject> setVisibility(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("visible_to") String visibile_to, @Field("visible_at") String visible_at, @Field("profile_id") String profile_id);
//    Call<SetVisibilityMainObject> setVisibility(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("visible_to") String visibile_to, @Field("visible_at") String visible_at);

    @FormUrlEncoded
    @POST("updateGroupVisibility")
    Call<SetVisibilityMainObject> setGroupVisibility(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("group_id") String group_id, @Field("sender_user_id") String sender_user_id, @Field("sender_profile_id") String sender_profile_id, @Field("visible_to") String visible_to, @Field("visible_at") String visible_at);
//    Call<SetVisibilityMainObject> setVisibility(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("visible_to") String visibile_to, @Field("visible_at") String visible_at);


    @FormUrlEncoded
    @POST("hideShowLocation")
    Call<RegisterLoginMainObject> hideShowLocation(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("source_user_id") String source_user_id, @Field("target_user_id") String target_user_id, @Field("set_location_to") String set_location_to, @Field("source_profile_id") String source_profile_id, @Field("target_profile_id") String target_profile_id);
//    Call<RegisterLoginMainObject> hideShowLocation(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("source_user_id") String source_user_id, @Field("target_user_id") String target_user_id, @Field("set_location_to") String set_location_to);

    @FormUrlEncoded
    @POST("updatePassword")
    Call<RegisterLoginMainObject> updatePassword(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("old_pass") String old_pass, @Field("new_pass") String new_pass);
//    Call<RegisterLoginMainObject> updatePassword(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("old_pass") String old_pass, @Field("new_pass") String new_pass);

    @FormUrlEncoded
    @POST("updateEmail")
    Call<RegisterLoginMainObject> updateEmail(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("old_email") String old_email, @Field("new_email") String new_email, @Field("user_pass") String user_pass);
//    Call<RegisterLoginMainObject> updateEmail(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("old_email") String old_email, @Field("new_email") String new_email, @Field("user_pass") String user_pass);

    @FormUrlEncoded
    @POST("updateUserPhone")
    Call<RegisterLoginMainObject> updateUserPhone(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("user_old_phone") String user_old_phone, @Field("user_new_phone") String user_new_phone);
//    Call<RegisterLoginMainObject> updateUserPhone(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("user_old_phone") String user_old_phone, @Field("user_new_phone") String user_new_phone);

    @FormUrlEncoded
    @POST("getChatThreads")
    Call<ChatThread> getUserChat(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("search_filters") String searchKey, @Field("search_key") String searchFilter);
//    Call<ChatThread> getUserChat(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("search_filters") String searchKey, @Field("search_key") String searchFilter);

    @FormUrlEncoded
    @POST("getChatThreadsNew")
    Call<ChatThread> getUserGroupChat(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("search_filters") String searchKey, @Field("search_key") String searchFilter, @Field("profile_id") String profile_id);

    @FormUrlEncoded
    @POST("sendMessage")
    Call<SendMessageMainObject> sendMessage(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("sender_id") String sender_id, @Field("receiver_id") String receiver_id, @Field("message") String message, @Field("sender_profile_id") String sender_profile_id, @Field("receiver_profile_id") String receiver_profile_id);

    @FormUrlEncoded
    @POST("sendMultiUserMessage")
    Call<SendMessageMainObject> sendMultiMessage(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("sender_id") String sender_id, @Field("receiver_ids") String receiver_id, @Field("message") String message, @Field("sender_profile_id") String sender_profile_id, @Field("receiver_profile_ids") String receiver_profile_ids);

    @FormUrlEncoded
    @POST("muteChat")
    Call<MuteChatMainObject> muteChat(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id_mute_by") String user_id_mute_by, @Field("user_id_mute_for") String user_id_mute_for, @Field("mute_type") int mute_type, @Field("mute_time") String mute_time, @Field("user_profile_id_mute_by") String user_profile_id_mute_by, @Field("user_profile_id_mute_for") String user_profile_id_mute_for);

    @FormUrlEncoded
    @POST("unmuteChat")
    Call<UnMuteChatMainObject> unMuteChat(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id_mute_by") String user_id_mute_by, @Field("user_id_mute_for") String user_id_mute_for, @Field("user_profile_id_mute_by") String user_profile_id_mute_by, @Field("user_profile_id_mute_for") String user_profile_id_mute_for);

    @FormUrlEncoded
    @POST("muteGroupChat")
    Call<MuteGroupChatMainObject> muteGroupChat(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("group_thread_id") String group_thread_id, @Field("user_id_mute_by") String user_id_mute_by, @Field("mute_type") int mute_type, @Field("mute_time") String mute_time, @Field("user_profile_id_mute_by") String user_profile_id_mute_by);

    @FormUrlEncoded
    @POST("unmuteGroupChat")
    Call<UnMuteGroupChatMainObject> unmuteGroupChat(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("group_thread_id") String group_thread_id, @Field("user_id_mute_by") String user_id_mute_by, @Field("user_profile_id_mute_by") String user_profile_id_mute_by);

    @FormUrlEncoded
    @POST("createGroupThread")
    Call<CreateGroupThreadMainObject> createGroupChat(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("participant_ids") String participant_ids, @Field("group_name") String group_name, @Field("participant_profile_ids") String participant_profile_ids);

    @FormUrlEncoded
    @POST("updateGroupName")
    Call<CreateGroupNameMainObject> createGroupName(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("thread_id") String group_thread_id, @Field("group_name") String group_name);

    @FormUrlEncoded
    @POST("sendGroupMessage")
    Call<SendMessageMainObject> sendGroupMessage(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("sender_id") String sender_id, @Field("thread_id") String thread_id, @Field("message") String message, @Field("sender_profile_id") String sender_profile_id);

    @FormUrlEncoded
    @POST("getGroupChat")
    Call<GetChatMainObject> groupChat(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("thread_id") String thread_id, @Field("user_id") String user_id, @Field("profile_id") String profile_id);

    @FormUrlEncoded
    @POST("sendShareContact")
    Call<ShareProfileMainObject> sendShareContact(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("sender_id") String sender_id, @Field("receiver_id") String receiver_id, @Field("user_id") String user_id, @Field("sender_profile_id") String sender_profile_id, @Field("receiver_profile_id") String receiver_profile_id, @Field("user_profile_id") String user_profile_id);

    @FormUrlEncoded
    @POST("sendGroupShareContact")
    Call<ShareGroupProfileMainObject> sendGroupShareContact(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("sender_id") String sender_id, @Field("thread_id") String thread_id, @Field("user_id") String user_id, @Field("sender_profile_id") String sender_profile_id, @Field("user_profile_id") String user_profile_id);

    @FormUrlEncoded
    @POST("addMemberToGroupChat")
    Call<AddGroupMemberMainObject> addMemberToGroupChat(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("thread_id") String thread_id, @Field("user_id") String user_id, @Field("profile_id") String profile_id, @Field("sender_user_id") String sender_user_id);

    @FormUrlEncoded
    @POST("leaveFromGroupChat")
    Call<AddGroupMemberMainObject> leaveFromGroupChat(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("thread_id") String thread_id, @Field("user_id") String user_id, @Field("profile_id") String profile_id);

    @Multipart
    @POST("sendAttachmentV2")
    Call<SendAttachementsModel> sendAttachment(@Header("x-api-key") String secret_key,
                                               @Header("Authorization") String baseAuth,
                                               @Part MultipartBody.Part filePart,
                                               @Part("sender_id") RequestBody sender_id,
                                               @Part("message") RequestBody message,
                                               @Part("receiver_id") RequestBody receiver_id);

    @Multipart
    @POST("sendMultipleAttachmentV2")
    Call<SendAttachementsModel> sendMultipleAttachment(@Header("x-api-key") String secret_key,
                                                       @Header("Authorization") String baseAuth,
                                                       @Part List<MultipartBody.Part> filePart,
                                                       @Part("sender_id") RequestBody sender_id,
                                                       @Part("message") RequestBody message,
                                                       @Part("receiver_id") RequestBody receiver_id,
                                                       @Part("sender_profile_id") RequestBody sender_profile_id,
                                                       @Part("receiver_profile_id") RequestBody receiver_profile_id
    );

    @Multipart
    @POST("sendVoiceAttachement")
    Call<SendAttachementsModel> sendVoiceAttachement(@Header("x-api-key") String secret_key,
                                              @Header("Authorization") String baseAuth,
                                              @Part("thread_id") RequestBody thread_id,
                                              @Part("sender_id") RequestBody sender_id,
                                              @Part("receiver_id") RequestBody receiver_id,
                                              @Part("sender_profile_id") RequestBody sender_profile_id,
                                              @Part("receiver_profile_id") RequestBody receiver_profile_id,
                                              @Part MultipartBody.Part filePart
    );

    @Multipart
    @POST("sendVoiceAttachementInGroup")
    Call<SendAttachementsModel> sendVoiceAttachementInGroup(@Header("x-api-key") String secret_key,
                                              @Header("Authorization") String baseAuth,
                                              @Part("thread_id") RequestBody thread_id,
                                              @Part("sender_id") RequestBody sender_id,
                                              @Part("sender_profile_id") RequestBody sender_profile_id,
                                              @Part MultipartBody.Part filePart
    );

//    @Multipart
//    @POST("sendAttachmentNew")
//    Call<SendAttachementsModel> sendAttachmentNew(@Header("x-api-key") String secret_key,
//                                               @Header("Authorization") String baseAuth,
//                                               @Part MultipartBody.Part filePart,
//                                               @Part("sender_id") RequestBody sender_id,
//                                               @Part("message") RequestBody message,
//                                               @Part("receiver_id") RequestBody receiver_id);

    @Multipart
    @POST("sendGroupAttachment")
    Call<SendAttachementsModel> sendGroupAttachment(@Header("x-api-key") String secret_key,
                                                    @Header("Authorization") String baseAuth,
                                                    @Part MultipartBody.Part filePart,
                                                    @Part("thread_id") RequestBody threadId,
                                                    @Part("sender_id") RequestBody senderId,
                                                    @Part("message") RequestBody message);

    @Multipart
    @POST("sendGroupMultipleAttachment")
    Call<SendAttachementsModel> sendGroupMultipleAttachment(@Header("x-api-key") String secret_key,
                                                            @Header("Authorization") String baseAuth,
                                                            @Part List<MultipartBody.Part> filePart,
                                                            @Part("thread_id") RequestBody threadId,
                                                            @Part("sender_id") RequestBody senderId,
                                                            @Part("message") RequestBody message,
                                                            @Part("sender_profile_id") RequestBody sender_profile_id);

    @Multipart
    @POST("sendMultiUserAttachment")
    Call<SendAttachementsModel> sendMultiAttachment(@Header("x-api-key") String secret_key,
                                                    @Header("Authorization") String baseAuth,
                                                    @Part MultipartBody.Part filePart,
                                                    @Part("sender_id") RequestBody sender_id,
                                                    @Part("message") RequestBody message,
                                                    @Part("receiver_ids") RequestBody receiver_id,
                                                    @Part("sender_profile_id") RequestBody sender_profile_id,
                                                    @Part("receiver_profile_ids") RequestBody receiver_profile_ids);

    @Multipart
    @POST("sendCsv")
    Call<ExportCSVModel> exportcsv(@Header("x-api-key") String secret_key,
                                   @Header("Authorization") String baseAuth,
                                   @Part MultipartBody.Part filePart,
                                   @Part("user_id") RequestBody user_id);


    @FormUrlEncoded
    @POST("resendMessage")
    Call<SendMessageMainObject> resendMessage(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("thread_id") String thread_id, @Field("msg_id") String msg_id, @Field("created_at") String created_at);
//    Call<SendMessageMainObject> resendMessage(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("thread_id") String thread_id, @Field("msg_id") String msg_id, @Field("created_at") String created_at);

    @FormUrlEncoded
    @POST("getChat")
    Call<GetChatMainObject> getChat(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("sender_id") String sender_id, @Field("receiver_id") String receiver_id, @Field("sender_profile_id") String sender_profile_id, @Field("receiver_profile_id") String receiver_profile_id);
//    Call<GetChatMainObject> getChat(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("sender_id") String sender_id, @Field("receiver_id") String receiver_id);

//    @FormUrlEncoded
//    @POST("getChatNew")
//    Call<GetChatMainObject> getChatNew(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("sender_id") String sender_id, @Field("receiver_id") String receiver_id);

    @FormUrlEncoded
    @POST("deleteAccount")
    Call<RegisterLoginMainObject> deleteAccount(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id);
//    Call<RegisterLoginMainObject> deleteAccount(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("referLNQ")
    Call<RegisterLoginMainObject> referLNQ(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("referral_fname") String refrral_fname, @Field("referral_lname") String referral_lname, @Field("referral_email") String referral_email, @Field("referral_phone") String referral_phone, @Field("referral_context") String referral_context, @Field("referral_notes") String referral_notes, @Field("referral_tasks") String referral_tasks);
//    Call<RegisterLoginMainObject> referLNQ(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("referral_fname") String refrral_fname, @Field("referral_lname") String referral_lname, @Field("referral_email") String referral_email, @Field("referral_phone") String referral_phone, @Field("referral_context") String referral_context, @Field("referral_notes") String referral_notes, @Field("referral_tasks") String referral_tasks);

    @FormUrlEncoded
    @POST("searchContactByName")
    Call<SearchUser> searchByName(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("search_key") String search_key, @Field("profile_id") String profile_id);
//    Call<SearchUser> searchByName(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("search_key") String search_key);

    @FormUrlEncoded
    @POST("doUserExist")
    Call<InviteUserMainObject> invite(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("logged_user_id") String logged_user_id, @Field("refrence_email") String refrence_email, @Field("refrence_phone") String refrence_phone);
//    Call<InviteUserMainObject> invite(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("logged_user_id") String logged_user_id, @Field("refrence_email") String refrence_email, @Field("refrence_phone") String refrence_phone);

    @FormUrlEncoded
    @POST("freezeUnfreezeAc")
    Call<RegisterLoginMainObject> freeze(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("freeze") String freeze);
//    Call<RegisterLoginMainObject> freeze(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("freeze") String freeze);

    @FormUrlEncoded
    @POST("freezeUnfreezeAc")
    Call<RegisterLoginMainObject> unFreeze(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("unfreeze") String unfreeze);
//    Call<RegisterLoginMainObject> unFreeze(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("unfreeze") String unfreeze);

    @FormUrlEncoded
    @POST("requestData")
    Call<RegisterLoginMainObject> requestData(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id);
//    Call<RegisterLoginMainObject> requestData(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("setUserSession")
    Call<RegisterLoginMainObject> setUserSession(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("action_type") String action_type);
//    Call<RegisterLoginMainObject> setUserSession(@Header("x-api-key") String secret_key, @Header("sent_at") String sent_at, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("action_type") String action_type);

    @FormUrlEncoded
    @POST("setDefaultSetting")
    Call<DefaultSetting> setDefaultSetting(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("home_view") String home_view, @Field("contact_view") String contact_view, @Field("profile_id") String profile_id);

    @FormUrlEncoded
    @POST("secondaryEmails")
    Call<RegisterLoginMainObject> secondaryEmails(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("secondary_emails") String secondary_emails);

    @FormUrlEncoded
    @POST("removeSecondaryEmails")
    Call<RegisterLoginMainObject> secondaryEmailsRemove(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("secondary_emails") String secondary_emails);

    @FormUrlEncoded
    @POST("socialLinks")
    Call<EditSocialLinksMainObject> socialLinks(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("social_links") String social_links, @Field("profile_id") String profile_id);

    @FormUrlEncoded
    @POST("removeSocialLinks")
    Call<EditSocialLinksMainObject> socialLinksRemove(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("social_links") String social_links, @Field("profile_id") String profile_id);

    @FormUrlEncoded
    @POST("secondaryPhones")
    Call<RegisterLoginMainObject> secondaryPhones(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("secondary_phones") String secondary_phones);

    @FormUrlEncoded
    @POST("removeSecondaryPhones")
    Call<RegisterLoginMainObject> secondaryPhonesRemove(@Header("x-api-key") String secret_key, @Header("Authorization") String baseAuth, @Field("user_id") String user_id, @Field("secondary_phones") String secondary_phones);

    //    @FormUrlEncoded
//    @POST("register")
//    Call<LoginSignup> signup(@Header("secret_key") String secret_key, @Field("full_name") String full_name, @Field("email") String email, @Field("password") String password, @Field("device_token") String device_token, @Field("device_type") String device_type);
//
//    @FormUrlEncoded
//    @POST("forgotpassword")
//    Call<LoginSignup> forgotpassword(@Header("secret_key") String secret_key, @Field("email") String email);
//
//    @FormUrlEncoded
//    @POST("facebook_login")
//    Call<LoginSignup> facebooklogin(@Header("secret_key") String secret_key, @Field("full_name") String full_name, @Field("email") String email, @Field("facebook_uid") String facebook_uid, @Field("device_token") String device_token, @Field("device_type") String device_type);
//
//    @FormUrlEncoded
//    @POST("referral")
//    Call<GenericModelStatusMessage> refer(@Header("secret_key") String secret_key, @Field("user_id") String user_id, @Field("email") String email, @Field("full_name") String full_name, @Field("site_name") String site_name, @Field("phone") String phone);
//
//    @FormUrlEncoded
//    @POST("send_mail")
//    Call<GenericModelStatusMessage> email(@Header("secret_key") String secret_key, @Field("user_id") String user_id, @Field("email") String email, @Field("phone") String phone);
//
//    @Multipart
//    @POST("update_profile")
//    Call<LoginSignup> editProfile(
//            @Header("secret_key") String secret_key,
//            @Part MultipartBody.Part filePart,
//            @Part("first_name") RequestBody firstName,
//            @Part("last_name") RequestBody lastName,
//            @Part("user_id") RequestBody user_id,
//            @Part("email") RequestBody email,
//            @Part("password") RequestBody password
//    );
//
//    @FormUrlEncoded
//    @POST("getusersites")
//    Call<UserSitesModel> getUserSites(@Header("secret_key") String secret_key, @Field("user_id") String user_id);
//
//    @FormUrlEncoded
//    @POST("gettemplates")
//    Call<UserTemplates> getTemplates(@Header("secret_key") String secret_key, @Field("user_id") String user_id);
//
//    @FormUrlEncoded
//    @POST("createsite")
//    Call<CreateSiteModel> createSite(@Header("secret_key") String secret_key, @Field("user_id") String user_id, @Field("site_name") String site_name);
//
//    @FormUrlEncoded
//    @POST("buildsite")
//    Call<BuildSiteModel> buildSite(@Header("secret_key") String secret_key, @Field("page_id") String page_id, @Field("site_id") String site_id, @Field("type") String type, @Field("new_page_id") String new_page_id);
//
//    @FormUrlEncoded
//    @POST("pushPageBlock")
//    Call<EditTemplateModel> loadTemplateBlocks(@Header("secret_key") String secret_key, @Field("site_id") String site_id, @Field("page_id") String pageid);
//
//    @FormUrlEncoded
//    @POST("getBlocksEditPage")
//    Call<BlocksComponentsModel> blocks(@Header("secret_key") String secret_key, @Field("user_id") String user_id);
//
//    @FormUrlEncoded
//    @POST("getComponentsEditPage")
//    Call<BlocksComponentsModel> components(@Header("secret_key") String secret_key, @Field("component_fetch") String componentsFetch);
//
//    @Multipart
//    @POST("uploadImage")
//    Call<TemplateImageUploadModel> uploadTemplateImage(
//            @Header("secret_key") String secret_key,
//            @Part MultipartBody.Part filePart
//    );
//
//    @FormUrlEncoded
//    @POST("getBlocksEditPageHtml")
//    Call<BlocksHtmlObject> getBlockHtml(@Header("secret_key") String secret_key, @Field("blocks_id") String block_id);
//
//    @FormUrlEncoded
//    @POST("saveHtmlEditPage")
//    Call<GenericModelStatusMessage> saveEditedTemplate(@Header("secret_key") String secret_key, @Field("updated_html") String data, @Field("save_temp_edit_page") String req, @Field("screenshotflag") String flag);
//
//    @FormUrlEncoded
//    @POST("creatSiteNewPage")
//    Call<CreatePageObject> createNewPage(@Header("secret_key") String secret_key, @Field("site_id") String site_id, @Field("page_name") String page_name);
//
//    @FormUrlEncoded
//    @POST("sitePagesList")
//    Call<SitePagesObject> getSitePages(@Header("secret_key") String secret_key, @Field("site_id") String site_id);

//    @GET("items.php/get_categories")
//    Call<HomeBuyerModel> buyerGetCategories();
//
//
//    @FormUrlEncoded
//    @POST("items.php/like_item")
//    Call<LikeApiModel>buyerLike(@Field("user_id") String user_id, @Field("item_id") String item_id, @Field("is_like") String is_like);
//
//
//    @FormUrlEncoded
//    @POST("items.php/unlike_item")
//    Call<LikeApiModel>buyerUnLike(@Field("user_id") String user_id, @Field("item_id") String item_id, @Field("is_like") String is_like);
//
//
//
//    @GET()
//    Call<HomeDetailModel> buyerGetCategoriesDetail(@Url String url);
//
//    @GET()
//    Call<ItemComment> buyerGetItemComment(@Url String url);
//
//    @FormUrlEncoded
//    @POST("items.php/add_cart_item")
//    Call<LikeApiModel>buyerAddToCart(@Field("buyer_id") String buyer_id, @Field("item_id") String item_id);
//
//
//
//    @GET()
//    Call<CartModel> buyerGetCart(@Url String url);
//
//    @GET()
//    Call<TruckModel> buyerGetTrucks(@Url String url);
//
//    @GET()
//    Call<HomeDetailModel> buyerTrucksDishes(@Url String url);
//
//    @FormUrlEncoded
//    @POST("items.php/add_to_fev")
//    Call<LikeApiModel>buyerAddFavourite(@Field("user_id") String user_id, @Field("item_id") String item_id);
//
//
//    @POST("order.php/check_out")
//    Call<OrderSumbitModel> RequestOrderSumbit(@Body InputOrderSumbit body);
//
//
//
//    @GET()
//    Call<HomeDetailModel> buyerGetFavouriteItems(@Url String url);
//
//    @GET()
//    Call<MyOrders> buyerGetMyOrders(@Url String url);
//
//    @Multipart
//    @POST("user.php/update_profile_image")
//    Call<ProfileUploadApiModel> buyerProfilePic(@Part MultipartBody.Part filePart, @Part("user_id") RequestBody user_id);
//
//
//    @FormUrlEncoded
//    @POST("messages.php/send_message")
//    Call<LikeApiModel>AddComment(@Field("buyer_id") String buyer_id, @Field("seller_id") String seller_id, @Field("message") String message, @Field("sender_type") String sender_type);
//
//
//    @GET()
//    Call<BothChats> buyerGetChats(@Url String url);
//
//    @GET()
//    Call<BothMessages> buyerGetMessages(@Url String url);
//
//    @FormUrlEncoded
//    @POST("user.php/update_profile")
//    Call<Profile>buyerProfileSave(@Field("user_id") String user_id, @Field("name") String name, @Field("email") String email, @Field("mobile") String mobile, @Field("details") String details);
//
//
//    @FormUrlEncoded
//    @POST("user.php/change_password")
//    Call<LikeApiModel>buyerChangePassword(@Field("user_id") String user_id, @Field("old_password") String old_password, @Field("password") String password);
//
//
//    @FormUrlEncoded
//    @POST("user.php/logout")
//    Call<LikeApiModel>buyerLogout(@Field("user_id") String user_id);
//
//
//    @FormUrlEncoded
//    @POST("user.php/change_notify")
//    Call<LikeApiModel>buyerNoti(@Field("user_id") String user_id, @Field("notify") String notify);
//
//    @GET()
//    Call<Chats> buyerGetThreads(@Url String url);
//
////    Seller Apis   //////////////////////////////////////////////////////////////////////////////////////////////////
//
//
//    @FormUrlEncoded
//    @POST("user.php/buyer_register")
//    Call<Registration>sellerRegistration(@Field("name") String name, @Field("username") String username, @Field("email") String email, @Field("password") String password, @Field("mobile") String mobile, @Field("seller") String seller, @Field("food_truck") String food_truck, @Field("token") String token, @Field("device") String device);
//
//
//    @FormUrlEncoded
//    @POST("user.php/login")
//    Call<Registration>sellerrLogin(@Field("email") String email, @Field("password") String password);
//
//
//
//    @GET()
//    Call<ManegeOrders> selerGetOrders(@Url String url);
//
//    @GET()
//    Call<MyItems> sellerGetItems(@Url String url);
//
//
//
//    @FormUrlEncoded
//    @POST("items.php/delete_item")
//    Call<LikeApiModel>sellerrDellMyItem(@Field("user_id") String user_id, @Field("item_id") String item_id);
//
//
//    @Multipart
//    @POST("items.php/add_item")
//    Call<LikeApiModel> RequestUploadItem(@Part MultipartBody.Part[] filePart, @Part("user_id") RequestBody user_id, @Part("category_id") RequestBody category_id, @Part("title") RequestBody title, @Part("description") RequestBody description, @Part("price") RequestBody price, @Part("delivery_cost") RequestBody delivery_cost, @Part("status") RequestBody status);
//
//    @Multipart
//    @POST("items.php/update_item/")
//    Call<LikeApiModel> RequestUpdateItem(@Part MultipartBody.Part[] filePart, @Part("user_id") RequestBody user_id, @Part("category_id") RequestBody category_id, @Part("title") RequestBody title, @Part("description") RequestBody description, @Part("price") RequestBody price, @Part("delivery_cost") RequestBody delivery_cost);
//
//    @FormUrlEncoded
//    @POST("order.php/set_order_status")
//    Call<LikeApiModel>sellerSetStatus(@Field("order_id") String order_id, @Field("status") String status);
//


//
//    @FormUrlEncoded
//    @POST("usergroups")
//    Call<FcmApiResponseModel> GroupSave(@Field("roomname") String roomname, @Field("roomjid") String roomjid, @Field("user_id") String user_id, @Field("role") String role, @Field("description") String description, @Field("color") String color);
//
//
//    @GET()
//    Call<GroupGet> RequestGroupFetch(@Url String url);
//

//    @Multipart
//    @POST()
//    Call<ProfilePicUpdateModel> ProfilePicUpdate(@Url String url, @Part MultipartBody.Part filePart);
//


//
//
//    @DELETE()
//    Call<DeleteFromCart> RequestDeleteFromCart(@Url String url);
//
//    @GET()
//    Call<UserTemplates> RequestGetWishList(@Url String url);
//
//    @POST("wishlist")
//    Call<AddToWishList> RequestAddToWishList(@Body WishListJsonModel body);
//
//    @GET()
//    Call<FeatureItems> RequestProductFeaturePagination(@Url String url);
//
//    @GET()
//    Call<DepartmentsItems> RequestProductPagination(@Url String url);
//
//
//    @GET("product")
//    Call<VoiceSearchModel> RequestVoiceSearch(@Query("keyword") String Word);
//
//    @POST("recently_viewed")
//    Call<AddToWishList> RequestRecentViewedAdd(@Body InputRecentViewedJson body);
//
//
//    @GET()
//    Call<RecentViewedItems> RequestRecentViewed(@Url String url);
//

//

//    @DELETE()
//    Call<AdminDeleteProductModel> RequestDeleteProduct(@Url String url);
//
//    @GET()
//    Call<AiDealsModel> RequestAiDealsPagination(@Url String url);
//
//    @GET()
//    Call<TodayDealModel> RequestTodaydealPagination(@Url String url);
//
//    @Multipart
//    @POST("product/search_products_by_image")
//    Call<ImageSearch> RequestImageSearch(@Part MultipartBody.Part filePart);
//
//    @POST("update_account_info")
//    Call<UpdateAccountResponseModel> RequestUpdateAccountInfo(@Body UpdateAccountInfoModel body);
//
//    @GET()
//    Call<Test> RequestYourOrders(@Url String url);
//
//    @GET()
//    Call<com.leadconcept.boai.LoginSignupForgotPasswordMainObject.kashif.Test> RequestOrdersReceived(@Url String url);
//
//    @POST("order/update_order")
//    Call<UpdateOrderResponseModel> RequestUpdateOrder(@Body UpdateOrderJsonModel body);
//
//
//    @POST("user")
//    Call<AdminDeleteProductModel> RequestAddUser(@Body InputJsonAdminAddUser body);
//
//    @Multipart
//    @POST("product/uplod_3d_image")
//    Call<AddToWishList> RequestUpload360Product(@Part MultipartBody.Part[] filePart, @Part("product_id") RequestBody product_id);
//
//
//
//

//    @FormUrlEncoded
//    @PUT()
//    Call<ProfileChange> updateProfileDetail(@Url String url, @Field("full_name") String full_name, @Field("email") String email, @Field("gender") String gender, @Field("phone_number") String phone_number, @Field("country") String country, @Field("theme_name") String theme_name, @Field("personal_info") String personal_info);
//
//
//    @FormUrlEncoded
//    @POST("groups")
//    Call<FcmApiResponseModel> GroupsSave(@Field("roomname") String roomname, @Field("roomjid") String roomjid, @Field("user_id") String user_id, @Field("role") String role, @Field("description") String description, @Field("color") String color);
//
//    @FormUrlEncoded
//    @POST("groups/admin")
//    Call<OpenChatAdminCheck> checkForAdmin(@Field("roomjid") String roomjid, @Field("user_id") String user_id);
//
//    @GET()
//    Call<ModelKickout> RequestKickout(@Url String url);
//
//    @POST("update_group")
//    Call<GroupInfoUpdateResponseModel> UpdateRoomInfoReq(@Body InputJsonRoomInfoUpdate body);
//
//    @POST("get_group_data")
//    Call<GroupInfoUpdatedResponse> UpdatedRoomInfoReq(@Body InputJsonRoomInfo body);
//
//    @Multipart
//    @POST()
//    Call<FcmApiResponseModel> requestUploadGroupSave(@Url String url,
//                                                     @Part MultipartBody.Part filePart,
//                                                     @Part("roomname") RequestBody roomname,
//                                                     @Part("roomjid") RequestBody roomjid,
//                                                     @Part("user_id") RequestBody user_id,
//                                                     @Part("role") RequestBody role,
//                                                     @Part("description") RequestBody description,
//                                                     @Part("color") RequestBody color
//    );
//

//    @FormUrlEncoded
//    @POST ("customers.php")
//    Call<CustomerModel> ReqCustomer(@Header("token") String token, @Field("company_id") String company_id);
//
//    @FormUrlEncoded
//    @POST("exchange_rate.php")
//    Call<RateModel> ReqExchangeRates(@Header("token") String token, @Field("company_id") String company_id);
//
//    @POST("logout.php")
//    Call<LogoutModel> ReqLogout(@Header("token") String token);
//
//    @FormUrlEncoded
//    @POST("sales.php")
//    Call<LogoutModel> ReqTransaction(@Header("token") String token, @Field("json") String json);

//    @GET("dancetypes")
//    Call<UserTemplates> getCategories(@Header("Authorizuser") String token);
//
//    @GET("events")
//    Call<E> getEvents(@Header("Authorizuser") String token, @Query("cat") String cat, @Query("subcat") String subcat);
//
//    @GET("members")
//    Call<UserModel> getMembers(@Header("oauth_secret") String oauth_secret, @Header("oauth_token") String oauth_token, @Header("oauth_consumer_key") String oauth_consumer_key, @Header("oauth_consumer_secret") String oauth_consumer_secret, @Query("page") String mPageNo);

    //    @GET("api/live-streams-videos")
//    Call<HomeVideos> getHomeVideos(@Header("Authorization") String token);
//    @GET("api/live-streams-videos")
//    Call<HomeVideos> getHomeVideos();
//    @FormUrlEncoded
//    @POST("login.php")
//    Call<ResponseModel> registerUser(@FieldMap Map<String, String> fields);
//

//
//    @FormUrlEncoded
//    @POST("api/accounts/social-login/")
//    Call<ResponseModel> loginSocialUser(@Field("provider") String provider,
//                                        @Field("access_token") String access_token,
//                                        @Field("access_token_secret") String access_token_secret);
//
//    @FormUrlEncoded
//    @POST("api/accounts/forget-password/")
//    Call<ResponseModel> forgotPassword(@Field("email") String email);
//
//    @GET("api/user/{userId}")
//    Call<UserProfile> getUserProfileData(@Path(value = "userId", encoded = false) String userId);
//
//    @GET("api/categories")
//    Call<Department> getCategories();
//
//    @GET("api/department/streams-videos/{categorySlug}")
//    Call<Department> getCategoryItems(@Header("Authorization") String token, @Path(value = "categorySlug", encoded = false) String categorySlug);
//
//    @FormUrlEncoded
//    @POST("api/follow-unfollow-user/")
//    Call<Follow> followUser(@Header("Authorization") String token, @Field("user_id") String user_id);
//
//    @FormUrlEncoded
//    @POST("api/check-follow-status/")
//    Call<ResponseModel> checkFollowStats(@Header("Authorization") String token, @Field("user_id") String user_id);
//
//    @FormUrlEncoded
//    @POST("api/update-stream-video-likes/")
//    Call<LikeCount> likeVideo(@Header("Authorization") String token, @Field("vos_id") String vos_id, @Field("likes") String likes);
//
//
//    @GET("api/user-total-likes/{user_id}")
//    Call<LikeCount> getUserLikes(@Path(value = "user_id", encoded = false) String user_id);
//
//    @FormUrlEncoded
//    @PUT("api/accounts/edit_information/")
//    Call<ResponseModel> updateProfileImage(@Header("Authorization") String token, @Field("image") String image);
}
package com.jk.codez;

import androidx.annotation.NonNull;

import com.jk.codez.item.Item;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class Network {
    private static final String CODEZ_URL = "http://api.joeknowles.com/codez";
    private static String USER_URL = "";
    private static final AsyncHttpClient client = new AsyncHttpClient();

    public static void setUrl(final String url) {
        USER_URL = url;
    }

//    public static String getUrl() {
//        if (USER_URL.isEmpty())
//            return CODEZ_URL;
//        return USER_URL;
//    }

    private static String getUrl(final String path) {
        if (USER_URL.equals("none"))
            return CODEZ_URL + path;
        return USER_URL + path;
    }

    public static void getItems(final TextHttpResponseHandler handler) {
        String url = getUrl("/get");
        client.get(url, handler);
    }

    public static void addItem(@NonNull final Item item, final TextHttpResponseHandler handler) {
        client.post(getUrl("/post"), createParams(item), handler);
    }

    public static void editItem(@NonNull final Item item, final TextHttpResponseHandler handler) {
        client.put(getUrl("/put"), createParams(item), handler);
    }

    public static void deleteItem(final String cid, final TextHttpResponseHandler handler) {
        client.delete(getUrl("/delete/" + cid), handler);
    }

    @NonNull
    private static RequestParams createParams(@NonNull final Item item) {
        RequestParams params = new RequestParams("cid", item._id);
        params.put("number", item.getNumber());
        params.put("street", item.getStreet());
        params.put("codes", item.getCodesString());
        params.put("notes", item.getNotes());
        params.put("lat", item.getLat());
        params.put("lng", item.getLng());
        params.put("precise", item.getPrecise());
        return params;
    }
}

//    }
//    private static final String USER_URL = "http://api.joeknowles.com/cx/user";
// GET
// POST /cx/auth
//    public static void authenticateUser(final String username, final String password, final AsyncHttpResponseHandler handler) {
//        System.out.printf("\nUsername: %s\nPassword: %s\n", username, password);
//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//        params.put("email", username);
//        params.put("password", password);
//        client.post(AUTH_URL, params, handler);
//    // PUT /cx/auth
//    public static void modifyToken(String token, final TextHttpResponseHandler handler) {

//         client.delete(AUTH_URL, new RequestParams("token", token), handler);

//    }

//    // GET /cx/user
//    public static void getAllUsers(final TextHttpResponseHandler handler) {
//         client.get(USER_URL, handler);
//    }
//
//    // GET /cx/user
//    public static void searchUsers(RequestParams params, final TextHttpResponseHandler handler) {
//         client.get(USER_URL, params, handler);
//    }
//
//    // POST /cx/user
//    public static void registerUser(final RequestParams formParams, final TextHttpResponseHandler handler) {
//         client.post(USER_URL, formParams, handler);
//    }
//
//    // DELETE /cx/user
//    public static void deleteUser(@NotNull final String e, final TextHttpResponseHandler handler) {
//         client.delete(USER_URL, new RequestParams("email", e), handler);
//    }
//
//    public static void updateUser(final RequestParams params, final TextHttpResponseHandler handler) {
//         client.put(USER_URL, params, handler);
//    }



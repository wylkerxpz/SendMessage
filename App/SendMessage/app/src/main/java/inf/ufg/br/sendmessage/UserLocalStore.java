package inf.ufg.br.sendmessage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by tundealao on 29/03/15.
 */
public class UserLocalStore {

    public static final String SP_NAME = "userDetails";

    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putInt("usuario_pk", user.usuario_pk);
        userLocalDatabaseEditor.putString("username", user.username);
        userLocalDatabaseEditor.putString("password", user.password);
        userLocalDatabaseEditor.putString("usuario_nome", user.usuario_nome);
        userLocalDatabaseEditor.putString("listaDisciplinas", user.listaDisciplinas);
        userLocalDatabaseEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
        userLocalDatabaseEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public User getLoggedInUser() {
        if (userLocalDatabase.getBoolean("loggedIn", false) == false) {
            return null;
        }

        int usuario_pk = userLocalDatabase.getInt("usuario_pk", -1);
        String username = userLocalDatabase.getString("username", "");
        String password = userLocalDatabase.getString("password", "");
        String usuario_nome = userLocalDatabase.getString("usuario_nome", "");
        String listaDisciplinas = userLocalDatabase.getString("listaDisciplinas", "");

        User user = new User(usuario_pk, username, password, usuario_nome, listaDisciplinas);
        return user;
    }
}

package inf.ufg.br.sendmessage;

import java.util.ArrayList;

public class User {

    int usuario_pk;
    String username, password, usuario_nome, listaDisciplinas;

    public User(int usuario_pk, String username, String password, String usuario_nome, String listaDisciplinas) {
        this.usuario_pk = usuario_pk;
        this.username = username;
        this.password = password;
        this.usuario_nome = usuario_nome;
        this.listaDisciplinas = listaDisciplinas;
    }

    public User(String username, String password) {
        this(-1, username, password, "", "");
    }
}

package br.com.sanara.model;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.wildfly.security.password.interfaces.BCryptPassword;

@Entity
@UserDefinition //qdo faz a requisição ela mostra q aqui tem o username e password
// só pode ser uma e a classe tem que ser jpa
public class Usuario extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cpf;

    // com essas anotações é posso usar essas variáveis com outro nome
    //pq a anotação faz a associaçao do atributo com a entidade role
    @Username
    private String username;
    @Password
    private String password;

    @Roles
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonbTransient // notação utilizada para não retornar o valor para o form e etc
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //adiciona a senha criptografada para um usuário e valida o seu username
    public static void adicionar(Usuario usuario) {
        usuario.password = BcryptUtil.bcryptHash(usuario.password);
        usuario.role = validarUsername(usuario.username);
    }

    private static String validarUsername(String username) {
        if (username.equals("admin")) {
            return "admin";
        } else {
            return "user";
        }
    }


}

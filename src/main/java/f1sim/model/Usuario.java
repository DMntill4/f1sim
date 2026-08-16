package f1sim.model;

public class Usuario {

    public enum Rol {
        ADMIN,
        USUARIO
    }

    public int id;
    public String username;
    public String password;
    public Rol rol;
    public String nombreCompleto;

    public Usuario() {
    }

    public Usuario(int id, String username, String password, Rol rol, String nombreCompleto) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.nombreCompleto = nombreCompleto;
    }
}

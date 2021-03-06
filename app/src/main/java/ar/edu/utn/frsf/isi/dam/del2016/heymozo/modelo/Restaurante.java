package ar.edu.utn.frsf.isi.dam.del2016.heymozo.modelo;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Restaurante {
    private static final String ID = "id";
    private static final String NOMBRE = "nombre";
    private static final String MONEDA = "moneda";
    private static final String LATITUD = "latitud";
    private static final String LONGITUD = "longitud";
	private static final String DIRECCION = "direccion";
	private static final String TELEFONO = "telefono";
	private static final String PAGINA = "pagina";
	private static final String RATING = "rating";
    private static final String IMAGEN = "imagen";
    private static final String IMAGEN64 = "imagen64";

	@SerializedName(ID)
	private String id;

    @SerializedName(NOMBRE)
    private String nombre;

    @SerializedName(MONEDA)
    private Moneda moneda;

    @SerializedName(LATITUD)
    private Double latitud;

    @SerializedName(LONGITUD)
    private Double longitud;

	@SerializedName(DIRECCION)
	private String direccion;

	@SerializedName(TELEFONO)
	private String telefono;

	@SerializedName(RATING)
	private Float rating;

	@SerializedName(PAGINA)
	private String pagina;

    @SerializedName(IMAGEN)
    private Imagen imagen;

	@SerializedName(IMAGEN64)
	private String imagen64;

    public Restaurante() {

    }

    public String toJSONObject() {
        return new Gson().toJson(this);
    }


	public String getId() {
		return id;
	}

	public String getNombre() {
        return nombre;
    }

    public Restaurante setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public Double getLatitud() {
        return latitud;
    }

    public Restaurante setLatitud(Double latitud) {
        this.latitud = latitud;
        return this;
    }

    public Double getLongitud() {
        return longitud;
    }

    public Restaurante setLongitud(Double longitud) {
        this.longitud = longitud;
        return this;
    }

    public Imagen getImagen() {
        return imagen;
    }

    public Restaurante setImagen(Imagen imagen) {
        this.imagen = imagen;
        return this;
    }

    public Restaurante setMoneda(Moneda moneda){
        this.moneda = moneda;
        return this;
    }

    public Moneda getMoneda(){
        return moneda;
    }

	public String getImagen64(){
		return imagen64;
	}

	public String getDireccion() {
		return direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public Float getRating() {
		return rating;
	}

	public String getPagina() {
		return pagina;
	}
}

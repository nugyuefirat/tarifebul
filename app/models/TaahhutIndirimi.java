package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="taahhutindirimi") 
public class TaahhutIndirimi extends Model {

	@ManyToOne
	public Tarife tarife;
	
	public int taahhutSuresi; // Ay
	public int indirimSuresi; // Ay
	public double indirimliUcret; // TL

	public TaahhutIndirimi(int taahhutSuresi, int indirimSuresi,
			double indirimliUcret) {
		super();
		this.taahhutSuresi = taahhutSuresi;
		this.indirimSuresi = indirimSuresi;
		this.indirimliUcret = indirimliUcret;
	}

	public TaahhutIndirimi() {
		// TODO Auto-generated constructor stub
	}

}

package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class AktivasyonUcreti extends Model {

	public long oncekiOperatorId;
	public long tarifeId;
	public double aktivasyonUcreti; // TL

	public AktivasyonUcreti(long oncekiOperatorId, long tarifeId,
			double aktivasyonUcreti) {
		super();
		this.oncekiOperatorId = oncekiOperatorId;
		this.tarifeId = tarifeId;
		this.aktivasyonUcreti = aktivasyonUcreti;
	}

}

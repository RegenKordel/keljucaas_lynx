package eu.openreq.keljucaas.domain.release;

import org.chocosolver.solver.variables.BoolVar;

public interface Diagnosable {
	public void  require(boolean include);
	public void unRequire();
	public BoolVar getIsIncluded();
	public String getNameId();
	public Integer getId();
}

package eu.openreq.keljucaas.domain;

import org.chocosolver.solver.variables.BoolVar;

public interface Diagnosable {
	public void  require(boolean include);
	public void unRequire();
	public BoolVar getIsIncluded();
}

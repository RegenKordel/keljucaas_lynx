package eu.openreq.keljucaas.domain;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;

import fi.helsinki.ese.murmeli.Relationship.NameType;

public abstract class Relationship4Csp implements Diagnosable {
	private Element4Csp from;
	private Element4Csp to;
	private BoolVar isIncluded;
	private boolean denyPosted = false;
	private boolean requirePosted = false;
	private Constraint requireCstr;
	private Constraint denyCstr;
	private Constraint relationShipConstraint = null;
	private Model model;
	private String id;
	
	public Relationship4Csp(Element4Csp from, Element4Csp to, Model model) {
		this.from = from;
		this.to = to;
		this.model = model;
	}
	
	protected void completeInitialization() {
		isIncluded = getRelationShipConstraint().reify();
		requireCstr = getModel().arithm(isIncluded, "=", 1);
		denyCstr = getModel().arithm(isIncluded, "=", 0);
		getModel().post(requireCstr);
		requirePosted = true;
	}


	public void require(boolean include) {
		if (include) {
			if (requirePosted) {
				return;
			}
			requireCstr.post();
			requirePosted = true;
			if (denyPosted) {
				getModel().unpost(denyCstr);
				denyPosted = false;
			}
		} else { // not include = deny
			if (denyPosted) {
				return;
			}
			denyCstr.post();
			denyPosted = true;
			if (requirePosted) {
				getModel().unpost(requireCstr);
				requirePosted = false;
			}

		}
	}

	public void unRequire() {
		if (denyPosted) {
			getModel().unpost(denyCstr);
			denyPosted = false;
		}
		if (requirePosted) {
			getModel().unpost(requireCstr);
			requirePosted = false;
		}
	}

	public BoolVar getIsIncluded() {
		return isIncluded;
	}

	protected abstract void makeConstraint();
	
	protected abstract String getRelationShipName();
	
	protected abstract boolean isSatisfiedWithAssignment(int releaseOfFrom, int ReleaseOfTo);

	@Override
	public String toString() {
		return "Relationship4Csp [from=" + getFrom() + ", relationshipType=" + getRelationShipName() + ", to=" + getTo() + "]";
	}

	public Constraint getRelationShipConstraint() {
		return relationShipConstraint;
	}

	public void setRelationShipConstraint(Constraint relationShipConstraint) {
		this.relationShipConstraint = relationShipConstraint;
	}

	public Model getModel() {
		return model;
	}

	public Element4Csp getFrom() {
		return from;
	}

	public Element4Csp getTo() {
		return to;
	}


//	public class RequiresRelationship4Csp extends Relationship4Csp {
//		
//		public RequiresRelationship4Csp(Element4Csp from, Element4Csp to, Model model) {
//			super (from, to, model);
//			makeConstraint();
//			completeInitialization();
//		}
//
//		protected void makeConstraint() {
//			// from is included implies (to is included and to.assignedRelease <=
//			// from.assignedrelease
//			setRelationShipConstraint(getModel().or(
//					getModel().arithm(getFrom().getIsIncluded(), "=", 0),
//					getModel().and(getModel().arithm(getTo().getIsIncluded(), "=", 1),
//							getModel().arithm(getTo().getAssignedContainer(), "<=", getFrom().getAssignedContainer()))));
//		}
//	}
//	
	
}

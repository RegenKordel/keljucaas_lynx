package eu.openreq.keljucaas.domain.release;

public class ReleasePlanAnalysisDefinition {
	private final String planName;
	private String analyzeOnlyIfIncosistentPlan;
	private  boolean diagnoseRequirements;
	private boolean diagnoseRelationships;
	private boolean omitCrossProject;
	


	public ReleasePlanAnalysisDefinition(String planName, boolean diagnoseRequirements,
			boolean diagnoseRelationships, boolean omitCrossProject) {
		super();
		this.planName = planName;
		this.diagnoseRequirements = diagnoseRequirements;
		this.diagnoseRelationships = diagnoseRelationships;
		this.omitCrossProject = omitCrossProject;
	}


	public String getAnalyzeOnlyIfIncosistentPlan() {
		return analyzeOnlyIfIncosistentPlan;
	}


	public void setAnalyzeOnlyIfIncosistentPlan(String analyzeOnlyIfIncosistentPlan) {
		this.analyzeOnlyIfIncosistentPlan = analyzeOnlyIfIncosistentPlan;
	}


	public String getPlanName() {
		return planName;
	}

	public boolean isDiagnoseRequirements() {
		return diagnoseRequirements;
	}


	public boolean isDiagnoseRelationships() {
		return diagnoseRelationships;
	}
	
	public boolean isDiagnoseDesired() {
		return diagnoseRequirements || diagnoseRelationships;
	}


	public final boolean isOmitCrossProject() {
		return omitCrossProject;
	}

}

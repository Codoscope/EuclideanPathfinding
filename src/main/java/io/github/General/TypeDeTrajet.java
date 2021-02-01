package main.java.io.github.General;


public enum TypeDeTrajet {
	ZonePourLeCalcul("Calculation area"),
	PlusCourtCheminOrthogonal("Shortest Manhattan path"),
	Zone("Shortened Manhattan area"),
	ZoneElargie("Euclidean area"),
	ZoneTopologique("Skeletonized area"),
	Graphe("Graph"),
	Corde("Graph with paths"),
	CordeLisse("Graph with smoothed paths"),
	UniquementCordeLisse("Smoothed paths");
	
	private TypeDeTrajet(String s) {
		this.s = s;
	}
	
	public String texteAssocie() {
		return s;
	}
	
	private String s;
};


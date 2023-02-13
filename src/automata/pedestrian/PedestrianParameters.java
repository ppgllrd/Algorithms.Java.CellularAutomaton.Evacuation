package automata.pedestrian;

/**
 * Class representing parameters describing a pedestrian.
 *
 * @param fieldAttractionBias how is the pedestrian attracted to exits.
 * @param crowdRepulsion      pedestrian's repulsion to get stuck in a position too crowded.
 * @author Pepe Gallardo
 */
public record PedestrianParameters(double fieldAttractionBias, double crowdRepulsion) {
  public static Builder Builder() {
    return new Builder();
  }

  public final static class Builder {
    double fieldAttractionBias = 1.0;
    double crowdRepulsion = 1.10;

    private Builder() {
    }

    public Builder fieldAttractionBias(double fieldAttractionBias) {
      this.fieldAttractionBias = fieldAttractionBias;
      return this;
    }

    public Builder crowdRepulsion(double crowdRepulsion) {
      this.crowdRepulsion = crowdRepulsion;
      return this;
    }

    public PedestrianParameters build() {
      return new PedestrianParameters(fieldAttractionBias, crowdRepulsion);
    }
  }
}

package automata.pedestrian;

/**
 * Class representing parameters describing a pedestrian.
 *
 * @param fieldAttractionBias how is the pedestrian attracted to exits.
 * @param crowdRepulsion      pedestrian's repulsion to get stuck in a position too crowded.
 * @author Pepe Gallardo
 */
public record PedestrianParameters(double fieldAttractionBias, double crowdRepulsion) {
  public static final class Builder {
    private double fieldAttractionBias = 1.0;
    private double crowdRepulsion = 1.10;

    public Builder() {
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

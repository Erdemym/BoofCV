package boofcv.alg.sfm.robust;

import boofcv.numerics.fitting.modelset.ModelMatcher;
import boofcv.numerics.fitting.modelset.ransac.Ransac;
import boofcv.struct.geo.PointPosePair;
import georegression.struct.point.Vector3D_F64;
import org.ejml.data.DenseMatrix64F;

import java.util.List;

/**
 * @author Peter Abeles
 */
public class ModelMatcherTranGivenRot implements ModelMatcher<Vector3D_F64,PointPosePair> {

	Ransac<Vector3D_F64,PointPosePair> alg;
	DistanceTranGivenRotSq dist = new DistanceTranGivenRotSq();
	TranGivenRotGenerator gen = new TranGivenRotGenerator();

	public ModelMatcherTranGivenRot(long randSeed, int maxIterations,
									double thresholdFit) {
		alg = new Ransac<Vector3D_F64, PointPosePair>(randSeed, gen, dist,
				maxIterations, thresholdFit);
	}

	public void setRotation( DenseMatrix64F R ) {
		dist.setRotation(R);
		gen.setRotation(R);
	}

	@Override
	public boolean process(List<PointPosePair> dataSet) {
		return alg.process(dataSet);
	}

	@Override
	public Vector3D_F64 getModel() {
		return alg.getModel();
	}

	@Override
	public List<PointPosePair> getMatchSet() {
		return alg.getMatchSet();
	}

	@Override
	public int getInputIndex(int matchIndex) {
		return alg.getInputIndex(matchIndex);
	}

	@Override
	public double getError() {
		return alg.getError();
	}
}

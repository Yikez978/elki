/*
 * This file is part of ELKI:
 * Environment for Developing KDD-Applications Supported by Index-Structures
 *
 * Copyright (C) 2017
 * ELKI Development Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.lmu.ifi.dbs.elki.algorithm.outlier;

import org.junit.Test;

import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

/**
 * Tests the GaussianUniformMixture algorithm.
 *
 * @author Lucia Cichella
 * @since 0.4.0
 */
public class GaussianUniformMixtureTest extends AbstractOutlierAlgorithmTest {
  @Test
  public void testGaussianUniformMixture() {
    Database db = makeSimpleDatabase(UNITTEST + "outlier-fire.ascii", 1025);

    // Parameterization
    ListParameterization params = new ListParameterization();

    // setup Algorithm
    GaussianUniformMixture<DoubleVector> gaussianUniformMixture = ClassGenericsUtil.parameterizeOrAbort(GaussianUniformMixture.class, params);
    testParameterizationOk(params);

    // run GaussianUniformMixture on database
    OutlierResult result = gaussianUniformMixture.run(db);

    testSingleScore(result, 1025, -20.27211494);
    testAUC(db, "Noise", result, 0.97251282);
  }
}
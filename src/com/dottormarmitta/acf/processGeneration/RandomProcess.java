package com.dottormarmitta.acf.processGeneration;

import com.dottormarmitta.acf.processModel.ProcessTenor;
import com.dottormarmitta.acf.time.Tenor;

public interface RandomProcess {
	
	/**
	 * This method returns realizations of the process
	 */
	public ProcessTenor getRealizations(Tenor tenorStructure);
	
	/**
	 * Get average value of the process over grid
	 */
	public double getAverage(Tenor tenorStructure);

}

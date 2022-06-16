package com.microstrategy.tools.integritymanager.comparator.legacy.analyzers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Class to analyze data
 * @author AWong
 *
 */
public class DataAnalyzer {

    private static final DataAnalyzer ANALYZER = new DataAnalyzer();

    static final String CURRENT_WORKFLOW = "com.microstrategy.MSTRTester.analyzers.DataAnalyzer";
	
	private DataAnalyzer() {
	}
	
	public static boolean isEquivalent(String lhsData, String rhsData, List<Interval> lhsDiffList, List<Interval> rhsDiffList) {
		return isEquivalent (new BufferedReader(new StringReader(lhsData)), new BufferedReader(new StringReader(rhsData)), lhsDiffList, rhsDiffList);
	}

	/**
	 * Takes two grid data (vector of vectors of strings) and return a 2-dimension boolean array 
	 * marking differential cells
	 * 
	 * @param lhsGridData first gridData to compare
	 * @param rhsGridData the other gridData to compare
	 * @return a 2-dimension boolean array identify which cells are different
	 * @throws Exception
	 */
	public static boolean[][] markDifferentGridCells (Vector<Vector<Object>> lhsGridData, Vector<Vector<Object>> rhsGridData, BooleanHolder isEquiv) throws Exception {
		
		boolean isEquivalent = true;
		if (lhsGridData == null || rhsGridData == null) //return null if either vector is null
		{
			if (isEquiv != null)
				isEquiv.setBoolean(isEquivalent);
			
			return null;
		}
		
		Vector<Vector<Object>> bigGridData = (lhsGridData.size() >= rhsGridData.size()) ? lhsGridData : rhsGridData;
		Vector<Vector<Object>> smallGridData = (lhsGridData.size() < rhsGridData.size()) ? lhsGridData : rhsGridData;
		int _maxRow = bigGridData.size();
		int _minRow = smallGridData.size();
		
		if (_maxRow != _minRow)
			isEquivalent = false;
		
		boolean isGridDataEquiv[][] = new boolean[_maxRow][];
		
		for (int i = 0; i < _maxRow; i++) {
			//if it is a excess row, mark all as different
			if (i >= _minRow) {
				isGridDataEquiv[i] = new boolean[bigGridData.get(i).size()];
				continue;
			}
			
			//otherwise, compare cell by cell
			Vector<Object> _lhsGridDataRow = lhsGridData.get(i);
			Vector<Object> _rhsGridDataRow = rhsGridData.get(i);
			Vector<Object> _bigGridDataRow = (_lhsGridDataRow.size() >= _rhsGridDataRow.size()) ? _lhsGridDataRow : _rhsGridDataRow;
			Vector<Object> _smallGridDataRow = (_lhsGridDataRow.size() < _rhsGridDataRow.size()) ? _lhsGridDataRow : _rhsGridDataRow;
			int _maxColumn = _bigGridDataRow.size();
			int _minColumn = _smallGridDataRow.size();
			
			if (_maxColumn != _minColumn)
				isEquivalent = false;
			
			isGridDataEquiv[i] = new boolean [_maxColumn];
			//mark all excess cells as different
			for (int j = 0; j < _minColumn; j++) {
				isGridDataEquiv[i][j] = ((String) _bigGridDataRow.get(j)).equals(_smallGridDataRow.get(j)); 
				isEquivalent = isEquivalent && isGridDataEquiv[i][j];
			}
		}	
		
		if (isEquiv != null)
			isEquiv.setBoolean(isEquivalent);
		
		return isGridDataEquiv;
	}

	public static boolean[][] markDifferentGridCells (List<List<Object>> lhsGridData, List<List<Object>> rhsGridData, BooleanHolder isEquiv) throws Exception {

		boolean isEquivalent = true;
		if (lhsGridData == null || rhsGridData == null) //return null if either vector is null
		{
			if (isEquiv != null)
				isEquiv.setBoolean(isEquivalent);

			return null;
		}

		List<List<Object>> bigGridData = (lhsGridData.size() >= rhsGridData.size()) ? lhsGridData : rhsGridData;
		List<List<Object>> smallGridData = (lhsGridData.size() < rhsGridData.size()) ? lhsGridData : rhsGridData;
		int _maxRow = bigGridData.size();
		int _minRow = smallGridData.size();

		if (_maxRow != _minRow)
			isEquivalent = false;

		boolean isGridDataEquiv[][] = new boolean[_maxRow][];

		for (int i = 0; i < _maxRow; i++) {
			//if it is a excess row, mark all as different
			if (i >= _minRow) {
				isGridDataEquiv[i] = new boolean[bigGridData.get(i).size()];
				continue;
			}

			//otherwise, compare cell by cell
			List<Object> _lhsGridDataRow = lhsGridData.get(i);
			List<Object> _rhsGridDataRow = rhsGridData.get(i);
			List<Object> _bigGridDataRow = (_lhsGridDataRow.size() >= _rhsGridDataRow.size()) ? _lhsGridDataRow : _rhsGridDataRow;
			List<Object> _smallGridDataRow = (_lhsGridDataRow.size() < _rhsGridDataRow.size()) ? _lhsGridDataRow : _rhsGridDataRow;
			int _maxColumn = _bigGridDataRow.size();
			int _minColumn = _smallGridDataRow.size();

			if (_maxColumn != _minColumn)
				isEquivalent = false;

			isGridDataEquiv[i] = new boolean [_maxColumn];
			//mark all excess cells as different
			for (int j = 0; j < _minColumn; j++) {
				isGridDataEquiv[i][j] = ((String) _bigGridDataRow.get(j)).equals(_smallGridDataRow.get(j));
				isEquivalent = isEquivalent && isGridDataEquiv[i][j];
			}
		}

		if (isEquiv != null)
			isEquiv.setBoolean(isEquivalent);

		return isGridDataEquiv;
	}
	
	public static boolean isEquivalent(BufferedReader lhsReader, BufferedReader rhsReader, List<Interval> lhsDiffList, List<Interval> rhsDiffList) {
		boolean isEquiv = true;
		
		try {			
			String lhsLine = null;
			String rhsLine = null;
			
			int lhsCurrIndex = 0;
			int rhsCurrIndex = 0;
			//boolean _bFirstLine = true;
			
			while (true) {
				lhsLine = lhsReader.readLine();
				rhsLine = rhsReader.readLine();
				
				if (lhsLine == null || rhsLine == null) break;

				ArrayList<Interval> lhsSubDiffList = new ArrayList<Interval>();
				ArrayList<Interval> rhsSubDiffList = new ArrayList<Interval>();
				
				if (!ANALYZER.isLineEquivalent(lhsLine, rhsLine, lhsSubDiffList, rhsSubDiffList) && isEquiv == true)
					isEquiv = false;

				ANALYZER.offsetDiffList(lhsCurrIndex, lhsSubDiffList);
				ANALYZER.offsetDiffList(rhsCurrIndex, rhsSubDiffList);
				
				lhsDiffList.addAll(lhsSubDiffList);
				rhsDiffList.addAll(rhsSubDiffList);
				
				//WLiao: TQMS 266755 remove first line.
				/*
				if (_bFirstLine) {
					lhsCurrIndex += (lhsLine.length()+1);
					rhsCurrIndex += (rhsLine.length()+1);
					
					_bFirstLine = false;
				}
				else {
					lhsCurrIndex += (lhsLine.length()+2);
					rhsCurrIndex += (rhsLine.length()+2);
				}
				*/
				lhsCurrIndex += (lhsLine.length()+2);
				rhsCurrIndex += (rhsLine.length()+2);
				
			}
			
			// left over rows
			if (lhsLine != null) {
				isEquiv = false;
				int beginIndex = lhsCurrIndex;
				
				do {
					lhsCurrIndex += lhsLine.length()+2;
				} while ((lhsLine = lhsReader.readLine()) != null);
				
				lhsDiffList.add(new Interval(beginIndex, lhsCurrIndex));
			} else if (rhsLine != null) {
				isEquiv = false;
				int beginIndex = rhsCurrIndex;
				
				do {
					rhsCurrIndex += rhsLine.length()+2;
				} while ((rhsLine = rhsReader.readLine()) != null);
				
				rhsDiffList.add(new Interval(beginIndex, rhsCurrIndex));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return isEquiv;
	}
	
	private boolean isLineEquivalent(String lhsData, String rhsData, List<Interval> lhsDiffList, List<Interval> rhsDiffList) {
		String[] lhsTokens = lhsData.split(";");
		String[] rhsTokens = rhsData.split(";");
		
		int lhsNumTokens = lhsTokens.length;
		int rhsNumTokens = rhsTokens.length;
		int minTokens = Math.min(lhsNumTokens, rhsNumTokens);
		boolean isEquiv = lhsNumTokens == rhsNumTokens ? true : false;
		int lhsCurrIndex = 0;
		int rhsCurrIndex = 0;
		int currToken = 0;
		
		for (currToken = 0; currToken < minTokens; currToken++) {
			if (!lhsTokens[currToken].trim().equals(rhsTokens[currToken].trim())) {
				if (isEquiv)
				    isEquiv = false;
				
				lhsDiffList.add(new Interval(lhsCurrIndex, lhsCurrIndex + lhsTokens[currToken].length() - 1));
				rhsDiffList.add(new Interval(rhsCurrIndex, rhsCurrIndex + rhsTokens[currToken].length() - 1));
			}

			lhsCurrIndex += lhsTokens[currToken].length() + 1; // add comma
			rhsCurrIndex += rhsTokens[currToken].length() + 1; // add comma
		}
		
		if (lhsNumTokens != rhsNumTokens) {
			if (lhsNumTokens > minTokens)
				lhsDiffList.add(new Interval(lhsCurrIndex, lhsData.length() - 1));
			else
				rhsDiffList.add(new Interval(rhsCurrIndex, rhsData.length() - 1));
		}
		
		return isEquiv;
	}
	
	private void offsetDiffList(int offset, List<Interval> diffList) {

        for (int i = 0; i < diffList.size(); ++i) {
            Interval originInterval = diffList.get(i);
            diffList.set(i, new Interval(originInterval.getStart() + offset, originInterval.getEnd() + offset));
        }
    }

    /**
     * This method is used compare two RwData and return root node of diff tree.
     * @param lhsExecutionResult first rmData
     * @param rhsExecutionResult second rwData
     * @param isEquiv flag indicates if two rwData are equivalent
     * @return
     */
	/*
    public static RWDataComparer populateDifferentRwData(ExecutionResult lhsExecutionResult,
                                                     ExecutionResult rhsExecutionResult, BooleanHolder isEquiv) {
        String lhsRwData = lhsExecutionResult.getRwData();

        String lengthId = lhsRwData == null ? "[null_rwd]" : String.valueOf(lhsRwData.length());
        PerformanceLogger.getLogger().debug(() -> PerformanceLogEntity.build().setWorkflowStart(CURRENT_WORKFLOW)
                .setObjectID(lengthId));
        RWDataComparer rwDataDiffs = new RWDataComparer(lhsExecutionResult, rhsExecutionResult);

        PerformanceLogger.getLogger().debug(() -> PerformanceLogEntity.build()
                .setWorkflow(CURRENT_WORKFLOW).setActionStart("deserialize_rwd_baseline").setObjectID(lengthId));
        rwDataDiffs.init();
        PerformanceLogger.getLogger().debug(() -> PerformanceLogEntity.build()
                .setWorkflow(CURRENT_WORKFLOW).setActionEnd("deserialize_rwd_baseline").setObjectID(lengthId));
        if (rwDataDiffs.isComparable()) {
            PerformanceLogger.getLogger().debug(
                () -> PerformanceLogEntity.build().setWorkflow(CURRENT_WORKFLOW).setActionStart("compare_rwd_data")
                        .setObjectID(lengthId));
            rwDataDiffs.populateComparisonResult();
            PerformanceLogger.getLogger().debug(
                () -> PerformanceLogEntity.build().setWorkflow(CURRENT_WORKFLOW).setActionEnd("compare_rwd_data")
                        .setObjectID(lengthId));
            RWDiffNode diffTree = rwDataDiffs.getRWDiffTree();
            if (diffTree != null) {
                isEquiv.setBoolean(diffTree.isEquivalent());
            }
            PerformanceLogger.getLogger().debug(() -> PerformanceLogEntity.build().setWorkflowEnd(CURRENT_WORKFLOW)
                    .setObjectID(lengthId));
			return rwDataDiffs;
		}
		return null;
	}

	 */
}

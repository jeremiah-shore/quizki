package com.haxwell.apps.questions.checkers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.haxwell.apps.questions.entities.Choice;
import com.haxwell.apps.questions.entities.Question;
import com.haxwell.apps.questions.utils.QuestionUtil;

public class SingleQuestionTypeChecker extends AbstractQuestionTypeChecker {

	public SingleQuestionTypeChecker(Question q) {
		this.question = q;
	}
	
	@Override
	public List<String> getKeysToPossibleUserSelectedAttributesInTheRequest() {
		ArrayList<String> list = new ArrayList<String>();
		
		list.add("group1");
		
		return list;
	}

	/**
	 * Returns true if the map of field names contains (as a value) the field name of the choice on the given question that has 
	 * indicated that it is CORRECT.
	 * 
	 * @param mapOfFieldNamesToValues a list of Strings, representing the field names that the user selected
	 * @return
	 */
	@Override
	public boolean questionIsCorrect(Map<String, String> mapOfFieldNamesToValues)
	{
		List<Choice> choices = QuestionUtil.getChoiceList(this.question);
		
		boolean rtn = true;
		
		for (Choice c : choices)
		{
			if (c.getIscorrect() > 0) 
				rtn &= mapOfFieldNamesToValues.containsValue(QuestionUtil.getFieldnameForChoice(question, c));
		}
		
		return rtn;
	}
}

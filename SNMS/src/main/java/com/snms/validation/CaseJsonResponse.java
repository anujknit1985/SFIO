package com.snms.validation;
import java.util.Map;

import com.snms.entity.CaseDetails;

public class CaseJsonResponse{
        private String status;
        private Map<String,String> errorsMap;
        private CaseDetails caseDetails;
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public Map<String,String> getErrorsMap() {
            return errorsMap;
        }
        public void setErrorsMap(Map<String,String> errorsMap) {
            this.errorsMap = errorsMap;
        }
		public CaseDetails getCaseDetails() {
			return caseDetails;
		}
		public void setCaseDetails(CaseDetails caseDetails) {
			this.caseDetails = caseDetails;
		}
       
    }
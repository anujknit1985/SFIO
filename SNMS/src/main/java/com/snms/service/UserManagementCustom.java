package com.snms.service;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.snms.dto.InspectorDTO;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.Inspector;
import com.snms.entity.OfficeOrder;
import com.snms.entity.RelationpersonCompany;
import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;
import com.snms.entity.personcompanyApproval;

@Repository
public interface UserManagementCustom {
	
	List<UserDetails> findByUnit(UnitDetails unitDetails);
	
	List<Object[]> findByCase(Long id);
	
	List<Object[]> findOfficeOrderPendingForApproval(int stage);
	List<Object[]> findOfficeOrderPendingForApproval();
	
	List<Company> findByCaseId(CaseDetails caseDetails);
	
	List<Object[]> findCaseByUserId(Long userId);

	Object[] findIoByCaseId(Long userId);

	/*List<Object[]> findNoticePendingForApproval(int stage);
	
	List<Object[]> findSummonPendingForApproval(int stage);*/
	List<Object[]> findNoticePendingForApproval(int stage,Long user_id);
	
	List<Object[]> findSummonPendingForApproval(int stage,Long user_id);
	
//	List<RelationpersonCompany> findpersonPendingListForApproval(int stage,Long user_id,int caseStage);
	List<personcompanyApproval> findpersonPendingListForApproval(int stage,Long user_id,int caseStage);
	List<personcompanyApproval> findpersonApprovedListForApproval(int stage,Long user_id,int caseStage);
	List<Object[]> getIoAdoByCaseId(Long caseId);

	//List<UserDetails> findByRole(int roleUser);

	int getCasesAssigned(Long userId);
	
	public boolean isAdo(Long userId);
	public List<UserDetails> findByRole(int roleUser,UnitDetails unitDetails);

	UserDetails findUserDetailsMobile(String mobile);
	List<Object[]> getInspectorList(Long caseId);
	

	List<Object[]> findByCaseId(Long id);

	List<Object[]> findDelInpBYCaseId(Long caseId);

	List<Object[]> findAllInspByCaseId(Long caseId);

	List<Company> findByAllCompByCaseId(CaseDetails caseDetails);

	List<Inspector> findByCaseandUserId(Long id, Long userId);

	List<Object[]> findpersonPendingForApproval(int i, Long userId);

	List<Object[]> findCaseByUserId(Long userId, Boolean isADO);

	List<Object[]> findCaseByUserIdbystage(Long userId);

	//List<RelationpersonCompany> findpersonPendingListForProApproval(int stage,Long user_id,int caseStage);
	List<personcompanyApproval> findpersonPendingListForProApproval(int stage,Long user_id,int caseStage);
	
	List<RelationpersonCompany> findpersonAorrovedListForProApproval(int stage,Long user_id,int caseStage);
	List<RelationpersonCompany> findTotalpersonListForProApproval(int stage,Long user_id,int caseStage);

	List<RelationpersonCompany> findApprovePersonListByUserID(int stage,Long user_id,int caseStage);

	List<RelationpersonCompany> findpersonPendingListByUserID(int stage,Long user_id,int caseStage);

	List<OfficeOrder> findAllByDirAprrovalStage1(boolean b, int i);

	List<OfficeOrder> findAllByAdoAprrovalStage1(boolean b, int i);

	List<Object[]> findPhySummonPendingForApproval(int i, Long userId, boolean isPhysicalOffline);

	List<Object[]> findPhyNoticePendingForApproval(int i, Long userId, boolean b);
	
	List<Object[]> findPhyNoticeSendBackforApproval(int i, Long userId, boolean b);

	List<Object[]> findPhySummonSendBackforApproval(int i, Long userId, boolean b);

	List<UserDetails> findByRole(int roleUser);

	List<OfficeOrder> findAllByAdoAprroval(boolean b);

	List<OfficeOrder> findAllByDirAprrovalStage1(boolean b);

	List<CaseDetails> findPeningForfarwarding();

	List<Company> findCompanyPendingForApproval(int stage, Long userId, boolean is_active, long approved_status);

	List<CaseDetails> findCompanyByCasePendingForApproval(int i, Long userId, boolean b, long l);

	List<Company> findByPendingCompByCaseId(CaseDetails caseDetails);

	List<CaseDetails> findCompanyByCasePendingForApprovalByADO(boolean is_active, long approved_status);

	List<Company> findByPendingCompByCaseId(CaseDetails caseDetails, String appRoleName);

	

	
}

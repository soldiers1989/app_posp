/**
 * 
 */
package xdt.dao.impl;

import org.springframework.stereotype.Repository;

import xdt.dao.OriginalOrderInfoDao;
import xdt.model.OriginalOrderInfo;

/**
 * @ClassName: OriginalOrderInfoDaoImpl
 * @Description:恒丰快捷支付 下游上送数据
 * @author LiShiwen
 * @date 2016年6月20日 下午4:15:11
 *
 */
@Repository
public class OriginalOrderInfoDaoImpl extends BaseDaoImpl<OriginalOrderInfo> implements OriginalOrderInfoDao {

	@Override
	public OriginalOrderInfo getOriginalOrderInfoByOrderid(String orderId) {
		String sql = this.getStatementId("getOriginalOrderInfoByOrderid"); 
		return sqlSession.selectOne(sql,orderId);
	}

	@Override
	public OriginalOrderInfo getOriginalOrderInfoByMerchanOrderId(String merchantOrderId) {
		String sql = this.getStatementId("getOriginalOrderInfoByMerchanOrderId"); 
		return sqlSession.selectOne(sql,merchantOrderId);
	}

	@Override
	public OriginalOrderInfo selectByOriginal(OriginalOrderInfo model) {
		String sql = this.getStatementId("selectByOriginal"); 
		return sqlSession.selectOne(sql,model);
	}
	@Override
	public OriginalOrderInfo selectByCjtOriginal(OriginalOrderInfo model) {
		
		String sql = this.getStatementId("selectByCjtOriginal"); 
		return sqlSession.selectOne(sql,model);
	}
	@Override
	public OriginalOrderInfo selectByCode(OriginalOrderInfo model) {
		// TODO Auto-generated method stub
		String sql = this.getStatementId("selectByCode"); 
		return sqlSession.selectOne(sql,model);
	}
	public OriginalOrderInfo getOriginalOrderInfoByUserId(String byUser) {
		// TODO Auto-generated method stub
		String sql = this.getStatementId("getOriginalOrderInfoByUserId"); 
		return sqlSession.selectOne(sql,byUser);
	}

}

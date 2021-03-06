package cn.hang.crm.modules.crm.service.impl;

import cn.hang.crm.common.entity.R;
import cn.hang.crm.common.entity.Result;
import cn.hang.crm.common.service.BaseService;
import cn.hang.crm.modules.crm.dao.CrmContactPOMapper;
import cn.hang.crm.modules.crm.entity.po.CrmContactPO;
import cn.hang.crm.modules.crm.service.CrmContactService;
import cn.hang.crm.modules.sys.entity.po.SysUserPO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lihang
 * @create 2017-11-25 下午2:27
 */
@Service
public class CrmContactServiceImpl extends BaseService implements CrmContactService {
    @Autowired
    private CrmContactPOMapper crmContactPOMapper;

    @Override
    public R addContact(SysUserPO loginUser, CrmContactPO crmContactPO) {
        crmContactPO.setChargeId(loginUser.getUserId());
        crmContactPO.setCreateBy(loginUser.getUserId());
        crmContactPOMapper.insertSelective(crmContactPO);
        return R.ok();
    }

    @Override
    public R updateContact(SysUserPO loginUser, CrmContactPO crmContactPO) {
        CrmContactPO localContact = crmContactPOMapper.selectByPrimaryKey(crmContactPO.getContactId());
        crmContactPO.setCreateBy(localContact.getCreateBy());
        crmContactPO.setChargeId(localContact.getChargeId());
        crmContactPOMapper.updateByPrimaryKeySelective(crmContactPO);
        return R.ok();
    }

    @Override
    public Result<CrmContactPO> getContactByContactId(long contactId) {
        return new Result<>(crmContactPOMapper.selectByPrimaryKey(contactId));
    }

    @Override
    public Result<PageInfo<CrmContactPO>> getUserContactList(SysUserPO loginUser, CrmContactPO crmContactPO, int type) {
        //本人负责的客户
        if (type == 1) {
            PageHelper.startPage(crmContactPO.getPage().getPageNum(), crmContactPO.getPage().getPageSize());
            Page<CrmContactPO> sysUserPOList = crmContactPOMapper.listMyChargedContactList(loginUser.getUserId());
            return new Result<>(new PageInfo<>(sysUserPOList));
        }
        //数据权限上能操作的客户
        if (type == 2) {
            crmContactPO.getSqlMap().put("dsf", dataScopeFilter(loginUser, "d", ""));
            PageHelper.startPage(crmContactPO.getPage().getPageNum(), crmContactPO.getPage().getPageSize());
            Page<CrmContactPO> sysUserPOList = crmContactPOMapper.listUserPermContactList(crmContactPO);
            return new Result<>(new PageInfo<>(sysUserPOList));
        }
        return new Result<>();
    }

    @Override
    public R deleteContactBatch(Long[] ids) {
        crmContactPOMapper.deleteBatch(ids);
        return R.ok();
    }
}

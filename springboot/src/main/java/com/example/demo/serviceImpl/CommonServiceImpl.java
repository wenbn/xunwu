package com.example.demo.serviceImpl;

import com.example.demo.dao.HsAdvertDataDao;
import com.example.demo.pojo.HsAdvertData;
import com.example.demo.serviceInter.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/22
 */
@Service("commonService")
@Transactional
public class CommonServiceImpl<T> implements CommonService<T> {


    @Autowired
    @Qualifier("hsAdvertDataDaoImpl")
    private HsAdvertDataDao hsAdvertDataDao;

    @Override
    public void addAdvertData(HsAdvertData hsAdvertData) {
        hsAdvertDataDao.insert(hsAdvertData);
    }
}

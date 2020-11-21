package com.udeam.edu.service.impl;

import com.udeam.edu.dao.AccountDao;
import com.udeam.edu.factory.ConnetionUtil;
import com.udeam.edu.factory.TransferServicemanager;
import com.udeam.edu.pojo.Account;
import com.udeam.edu.service.TransferService;

/**
 * @author 应癫
 */
public class TransferServiceImpl implements TransferService {

    // 1 原始的new 方法创建dao接口实现类对象
    //private AccountDao accountDao = new JdbcAccountDaoImpl();

    //2 从ioc bean工厂获取
    //private AccountDao accountDao = (AccountDao) BeanFactorys.getBean("accountDao");

    // 3 从ioc bean工厂获取,并且set 赋值注入
    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }


    /**
     *
     * 当前业务层没有事务,两次update 属于同一个现成类的执行调用,两次update是两次数据库连接操作,
     * 没有在一个数据库连接中进行,不属于一个事务中 (数据库事务属于同一个连接的操作事务)
     * 所以当出现异常时,当前线程内的不同的数据库连接进行不同的操作,当前方法内数据不能一致,导致数据异常;
     *
     * 解决思路:让当前线程2个update操作内的数据库操作属于同一个`线程`内执行调用,可以让这个线程使用同一个数据库连接
     *  可以使用ThreadLocal 将数据库连接绑定在同一个线程上
     *
     *
     *
     *
     *
     *
     *
     * @param fromCardNo
     * @param toCardNo
     * @param money
     * @throws Exception
     */
    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {

        try {

            //开启事务
            TransferServicemanager.get().start();


            Account from = accountDao.queryAccountByCardNo(fromCardNo);
            Account to = accountDao.queryAccountByCardNo(toCardNo);

            from.setMoney(from.getMoney()-money);
            to.setMoney(to.getMoney()+money);

            accountDao.updateAccountByCardNo(to);
            //模拟异常代码
            int c = 1/0;
            accountDao.updateAccountByCardNo(from);
            //提交事务
            TransferServicemanager.get().commit();
        }catch (Exception e){
            //回滚事务
            TransferServicemanager.get().rowback();
            System.out.println(e.getStackTrace());
            throw new RuntimeException("失败!");
        }finally {
            //关闭连接
            System.out.println(ConnetionUtil.getconnetionUtil());
            ConnetionUtil.getconnetionUtil().close();
        }

    }


}

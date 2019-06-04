package com.linweiyuan.shadowsocks.repository;

import com.linweiyuan.shadowsocks.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

}

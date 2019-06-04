package com.linweiyuan.shadowsocks.repository;

import com.linweiyuan.shadowsocks.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Page<Account> findByIpLikeOrPortLikeOrPasswordLikeOrMethodLikeOrLocationLikeOrConfigLikeOrStatusLike(
            String ip, String port, String password, String method, String location, String config, String status, Pageable pageable);
}

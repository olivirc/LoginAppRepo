package com.ms.loginapplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.ms.loginapplication.model.AuthenticationTokensData;

@Repository
public interface AuthenticationTokensDataRepository extends JpaRepository<AuthenticationTokensData, Long>{
	
	@Query("FROM AuthenticationTokensData where api_key = ?1")
	AuthenticationTokensData findByuserToken(String apiKey);
	
	@Transactional
	@Modifying(clearAutomatically = true)
    @Query("UPDATE AuthenticationTokensData atd SET atd.serviceToken = :serviceToken , atd.transactionToken = :transactionToken WHERE atd.userToken = :userToken ")
	int updateConvFactor(@Param("userToken") String convFactor, @Param("serviceToken") String serviceToken,@Param("transactionToken") String transactionToken);

	@Transactional
	@Modifying(clearAutomatically = true)
    @Query("DELETE FROM AuthenticationTokensData atd  WHERE atd.apiKey = :apiKey ")
	int deleteAuthenticationData(@Param("apiKey") String apiKey);

	
}

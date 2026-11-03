package com.ibm.capstone.ebookstore.service;

import com.ibm.capstone.ebookstore.dto.AddressRequest;
import com.ibm.capstone.ebookstore.entity.Address;
import com.ibm.capstone.ebookstore.entity.User;
import com.ibm.capstone.ebookstore.exception.ResourceNotFoundException;
import com.ibm.capstone.ebookstore.repository.AddressRepository;
import com.ibm.capstone.ebookstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<Address> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    @Transactional
    public Address addAddress(Long userId, AddressRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Address> existing = addressRepository.findByUserId(userId);
        boolean isFirst = existing.isEmpty();

        Address address = new Address(
                req.getRecipientName(),
                req.getStreet(),
                req.getCity(),
                req.getState(),
                req.getPostalCode(),
                req.getCountry(),
                req.isDefault() || isFirst,
                user
        );

        if (req.isDefault() && !isFirst) {
            existing.forEach(a -> a.setDefault(false));
            addressRepository.saveAll(existing);
        }

        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address does not belong to user");
        }
        addressRepository.delete(address);
    }
}

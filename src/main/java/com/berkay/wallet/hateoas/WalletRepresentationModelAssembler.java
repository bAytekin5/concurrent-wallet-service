package com.berkay.wallet.hateoas;

import com.berkay.wallet.controller.WalletController;
import com.berkay.wallet.dto.WalletResponse;
import com.berkay.wallet.entity.Wallet;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class WalletRepresentationModelAssembler
        extends RepresentationModelAssemblerSupport<Wallet, WalletResponse> {

    public WalletRepresentationModelAssembler(Class<?> controllerClass, Class<WalletResponse> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    public WalletResponse toModel(Wallet entity) {
        WalletResponse response = instantiateModel(entity);
        response.setId(entity.getId());
        response.setUserId(entity.getUser().getId());
        response.setCurrency(entity.getCurrency());
        response.setCreatedAt(entity.getCreatedAt());

        response.add(linkTo(
                methodOn(WalletController.class).getWalletById(entity.getId().toString()))
                .withSelfRel());

        return response;
    }
}

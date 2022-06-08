/*
 * Sincere-Loyalty
 * Copyright (C) 2020 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package ladysnake.sincereloyalty.mixin;

import ladysnake.sincereloyalty.LoyalTrident;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Nullable
    @Unique
    private Boolean veryLoyalTrident;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    @Shadow
    public abstract boolean cannotPickup();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tickItem(CallbackInfo ci) {
        // Only spawn a trident if it has a pickup delay (usually sign of it being dropped by a player)
        if (!this.world.isClient && this.cannotPickup()) {
            if (this.veryLoyalTrident == null) {
                this.veryLoyalTrident = LoyalTrident.hasTrueOwner(this.getStack());
            }
            if (this.veryLoyalTrident) {
                TridentEntity tridentEntity = LoyalTrident.spawnTridentForStack(this, this.getStack());
                if (tridentEntity != null) {
                    LoyalTrident.of(tridentEntity).loyaltrident_sit();
                    this.discard();
                    ci.cancel();
                }
            }
        }
    }
}

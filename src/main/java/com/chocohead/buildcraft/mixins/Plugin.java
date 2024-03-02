package com.chocohead.buildcraft.mixins;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class Plugin implements IMixinConfigPlugin {
	public static final int RENDER_BLOCKS_TYPE_START = 40;
	public static final BitSet AVAILABLE_RENDER_TYPES = new BitSet();
	public static final String RENDER_BLOCKS_MAGIC = "📦 BUILDCRAFT-HOOK 📦";

	@Override
	public void onLoad(String mixinPackage) {
		
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public List<String> getMixins() {
		return Collections.emptyList();
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		if ("RenderBlocksMixin".equals(mixinInfo.getName())) {
			for (MethodNode method : targetClass.methods) {
				if ("renderBlockByRenderType".equals(method.name) && "(Lnet/minecraft/src/game/block/Block;III)Z".equals(method.desc)) {
					for (AbstractInsnNode insn : method.instructions) {
						if (insn.getType() == AbstractInsnNode.TABLESWITCH_INSN) {
							TableSwitchInsnNode table = (TableSwitchInsnNode) insn;

							LabelNode bail = new LabelNode();
							int type = RENDER_BLOCKS_TYPE_START;
							for (ListIterator<LabelNode> it = table.labels.listIterator(RENDER_BLOCKS_TYPE_START - table.min); it.hasNext(); type++) {
								if (it.next() == table.dflt) {
									it.set(bail);
									AVAILABLE_RENDER_TYPES.set(type);
								}
							}

							InsnList extra = new InsnList();
							extra.add(bail);
							extra.add(new LdcInsnNode(RENDER_BLOCKS_MAGIC));
							extra.add(new InsnNode(Opcodes.POP));
							method.instructions.insertBefore(table.dflt, extra);
							break;
						}
					}
					break;
				}
			}
		}
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		
	}
}
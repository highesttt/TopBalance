package xyz.efekurbann.topbalance.utils.UUIDRes;

import xyz.efekurbann.topbalance.utils.UUIDRes.UUIDProperties;

import java.util.UUID;

import xyz.efekurbann.topbalance.utils.UUIDRes.UUIDMeta;

@SuppressWarnings("unused")
public class UUIDPlayerData {
	public String id;
	public UUID raw_id;
	public String username;
    public String avatar;
    public String skin_texture;
    public String[] name_history;
    public UUIDProperties[] properties;
    public UUIDMeta meta;
}
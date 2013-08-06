<?php

function _sanitise($str) {
	$str = preg_replace("/[^a-zA-Z0-9]+/", '', $str);
	if($str == '')
		$str = 'unknown';
	
	return $str;
}

if(!isset($_GET['ashdi']) || $_GET['ashdi'] != 'sdHIsklej89')
	die('invalid key');

$post = file_get_contents('php://input');
$client = 'unknown';
$level = 'untitled';

if(isset($_GET['client']))
	$client = _sanitise($_GET['client']);
if(isset($_GET['level']))
	$level = _sanitise($_GET['level']);

if(!file_exists('store/' . $level))
	mkdir('store/' . $level);

file_put_contents(
	'store/' . $level . '/stat-'
	 . $client . '-'
	 . time() . '-'
	 . rand() . '.txt', $post);

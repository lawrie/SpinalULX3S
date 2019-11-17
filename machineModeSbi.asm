
build/machineModeSbi.elf:     file format elf32-littleriscv


Disassembly of section .init:

80000000 <trapEntry>:
80000000:	34011173          	csrrw	sp,mscratch,sp
80000004:	00112223          	sw	ra,4(sp)
80000008:	00312623          	sw	gp,12(sp)
8000000c:	00412823          	sw	tp,16(sp)
80000010:	00512a23          	sw	t0,20(sp)
80000014:	00612c23          	sw	t1,24(sp)
80000018:	00712e23          	sw	t2,28(sp)
8000001c:	02812023          	sw	s0,32(sp)
80000020:	02912223          	sw	s1,36(sp)
80000024:	02a12423          	sw	a0,40(sp)
80000028:	02b12623          	sw	a1,44(sp)
8000002c:	02c12823          	sw	a2,48(sp)
80000030:	02d12a23          	sw	a3,52(sp)
80000034:	02e12c23          	sw	a4,56(sp)
80000038:	02f12e23          	sw	a5,60(sp)
8000003c:	05012023          	sw	a6,64(sp)
80000040:	05112223          	sw	a7,68(sp)
80000044:	05212423          	sw	s2,72(sp)
80000048:	05312623          	sw	s3,76(sp)
8000004c:	05412823          	sw	s4,80(sp)
80000050:	05512a23          	sw	s5,84(sp)
80000054:	05612c23          	sw	s6,88(sp)
80000058:	05712e23          	sw	s7,92(sp)
8000005c:	07812023          	sw	s8,96(sp)
80000060:	07912223          	sw	s9,100(sp)
80000064:	07a12423          	sw	s10,104(sp)
80000068:	07b12623          	sw	s11,108(sp)
8000006c:	07c12823          	sw	t3,112(sp)
80000070:	07d12a23          	sw	t4,116(sp)
80000074:	07e12c23          	sw	t5,120(sp)
80000078:	07f12e23          	sw	t6,124(sp)
8000007c:	4f8000ef          	jal	ra,80000574 <trap>
80000080:	00412083          	lw	ra,4(sp)
80000084:	00c12183          	lw	gp,12(sp)
80000088:	01012203          	lw	tp,16(sp)
8000008c:	01412283          	lw	t0,20(sp)
80000090:	01812303          	lw	t1,24(sp)
80000094:	01c12383          	lw	t2,28(sp)
80000098:	02012403          	lw	s0,32(sp)
8000009c:	02412483          	lw	s1,36(sp)
800000a0:	02812503          	lw	a0,40(sp)
800000a4:	02c12583          	lw	a1,44(sp)
800000a8:	03012603          	lw	a2,48(sp)
800000ac:	03412683          	lw	a3,52(sp)
800000b0:	03812703          	lw	a4,56(sp)
800000b4:	03c12783          	lw	a5,60(sp)
800000b8:	04012803          	lw	a6,64(sp)
800000bc:	04412883          	lw	a7,68(sp)
800000c0:	04812903          	lw	s2,72(sp)
800000c4:	04c12983          	lw	s3,76(sp)
800000c8:	05012a03          	lw	s4,80(sp)
800000cc:	05412a83          	lw	s5,84(sp)
800000d0:	05812b03          	lw	s6,88(sp)
800000d4:	05c12b83          	lw	s7,92(sp)
800000d8:	06012c03          	lw	s8,96(sp)
800000dc:	06412c83          	lw	s9,100(sp)
800000e0:	06812d03          	lw	s10,104(sp)
800000e4:	06c12d83          	lw	s11,108(sp)
800000e8:	07012e03          	lw	t3,112(sp)
800000ec:	07412e83          	lw	t4,116(sp)
800000f0:	07812f03          	lw	t5,120(sp)
800000f4:	07c12f83          	lw	t6,124(sp)
800000f8:	34011173          	csrrw	sp,mscratch,sp
800000fc:	30200073          	mret

80000100 <_start>:
80000100:	00003117          	auipc	sp,0x3
80000104:	a4810113          	addi	sp,sp,-1464 # 80002b48 <_sp>
80000108:	00001517          	auipc	a0,0x1
8000010c:	9d450513          	addi	a0,a0,-1580 # 80000adc <__init_array_end>
80000110:	00001597          	auipc	a1,0x1
80000114:	9cc58593          	addi	a1,a1,-1588 # 80000adc <__init_array_end>
80000118:	00001617          	auipc	a2,0x1
8000011c:	a3060613          	addi	a2,a2,-1488 # 80000b48 <__bss_start>
80000120:	00c5fc63          	bgeu	a1,a2,80000138 <_start+0x38>
80000124:	00052283          	lw	t0,0(a0)
80000128:	0055a023          	sw	t0,0(a1)
8000012c:	00450513          	addi	a0,a0,4
80000130:	00458593          	addi	a1,a1,4
80000134:	fec5e8e3          	bltu	a1,a2,80000124 <_start+0x24>
80000138:	00001517          	auipc	a0,0x1
8000013c:	a1050513          	addi	a0,a0,-1520 # 80000b48 <__bss_start>
80000140:	00001597          	auipc	a1,0x1
80000144:	a0858593          	addi	a1,a1,-1528 # 80000b48 <__bss_start>
80000148:	00b57863          	bgeu	a0,a1,80000158 <_start+0x58>
8000014c:	00052023          	sw	zero,0(a0)
80000150:	00450513          	addi	a0,a0,4
80000154:	feb56ce3          	bltu	a0,a1,8000014c <_start+0x4c>
80000158:	0dd000ef          	jal	ra,80000a34 <__libc_init_array>
8000015c:	0dc000ef          	jal	ra,80000238 <init>
80000160:	00000097          	auipc	ra,0x0
80000164:	01408093          	addi	ra,ra,20 # 80000174 <done>
80000168:	00000513          	li	a0,0
8000016c:	80bf05b7          	lui	a1,0x80bf0
80000170:	30200073          	mret

80000174 <done>:
80000174:	0000006f          	j	80000174 <done>

80000178 <_init>:
80000178:	00008067          	ret

Disassembly of section .text:

8000017c <putHex>:
8000017c:	fe010113          	addi	sp,sp,-32
80000180:	00812c23          	sw	s0,24(sp)
80000184:	00912a23          	sw	s1,20(sp)
80000188:	01212823          	sw	s2,16(sp)
8000018c:	01312623          	sw	s3,12(sp)
80000190:	00112e23          	sw	ra,28(sp)
80000194:	00050913          	mv	s2,a0
80000198:	01c00413          	li	s0,28
8000019c:	00900993          	li	s3,9
800001a0:	ffc00493          	li	s1,-4
800001a4:	408957b3          	sra	a5,s2,s0
800001a8:	00f7f793          	andi	a5,a5,15
800001ac:	0ff7f713          	andi	a4,a5,255
800001b0:	03070513          	addi	a0,a4,48
800001b4:	00f9d463          	bge	s3,a5,800001bc <putHex+0x40>
800001b8:	03770513          	addi	a0,a4,55
800001bc:	ffc40413          	addi	s0,s0,-4
800001c0:	7f4000ef          	jal	ra,800009b4 <putC>
800001c4:	fe9410e3          	bne	s0,s1,800001a4 <putHex+0x28>
800001c8:	01c12083          	lw	ra,28(sp)
800001cc:	01812403          	lw	s0,24(sp)
800001d0:	01412483          	lw	s1,20(sp)
800001d4:	01012903          	lw	s2,16(sp)
800001d8:	00c12983          	lw	s3,12(sp)
800001dc:	02010113          	addi	sp,sp,32
800001e0:	00008067          	ret

800001e4 <putString>:
800001e4:	ff010113          	addi	sp,sp,-16
800001e8:	00812423          	sw	s0,8(sp)
800001ec:	00112623          	sw	ra,12(sp)
800001f0:	00050413          	mv	s0,a0
800001f4:	00044503          	lbu	a0,0(s0)
800001f8:	00051a63          	bnez	a0,8000020c <putString+0x28>
800001fc:	00c12083          	lw	ra,12(sp)
80000200:	00812403          	lw	s0,8(sp)
80000204:	01010113          	addi	sp,sp,16
80000208:	00008067          	ret
8000020c:	7a8000ef          	jal	ra,800009b4 <putC>
80000210:	00140413          	addi	s0,s0,1
80000214:	fe1ff06f          	j	800001f4 <putString+0x10>

80000218 <setup_pmp>:
80000218:	01f00793          	li	a5,31
8000021c:	fff00713          	li	a4,-1
80000220:	00000297          	auipc	t0,0x0
80000224:	01428293          	addi	t0,t0,20 # 80000234 <setup_pmp+0x1c>
80000228:	30529073          	csrw	mtvec,t0
8000022c:	3b071073          	csrw	pmpaddr0,a4
80000230:	3a079073          	csrw	pmpcfg0,a5
80000234:	00008067          	ret

80000238 <init>:
80000238:	ff010113          	addi	sp,sp,-16
8000023c:	00112623          	sw	ra,12(sp)
80000240:	fd9ff0ef          	jal	ra,80000218 <setup_pmp>
80000244:	7ec000ef          	jal	ra,80000a30 <halInit>
80000248:	80001537          	lui	a0,0x80001
8000024c:	adc50513          	addi	a0,a0,-1316 # 80000adc <_sp+0xffffdf94>
80000250:	f95ff0ef          	jal	ra,800001e4 <putString>
80000254:	800007b7          	lui	a5,0x80000
80000258:	00078793          	mv	a5,a5
8000025c:	30579073          	csrw	mtvec,a5
80000260:	800037b7          	lui	a5,0x80003
80000264:	ac878793          	addi	a5,a5,-1336 # 80002ac8 <_sp+0xffffff80>
80000268:	34079073          	csrw	mscratch,a5
8000026c:	000017b7          	lui	a5,0x1
80000270:	88078793          	addi	a5,a5,-1920 # 880 <__stack_size-0x1780>
80000274:	30079073          	csrw	mstatus,a5
80000278:	30405073          	csrwi	mie,0
8000027c:	804007b7          	lui	a5,0x80400
80000280:	34179073          	csrw	mepc,a5
80000284:	0000b7b7          	lui	a5,0xb
80000288:	10078793          	addi	a5,a5,256 # b100 <__stack_size+0x9100>
8000028c:	30279073          	csrw	medeleg,a5
80000290:	22200793          	li	a5,546
80000294:	30379073          	csrw	mideleg,a5
80000298:	14305073          	csrwi	stval,0
8000029c:	00c12083          	lw	ra,12(sp)
800002a0:	80001537          	lui	a0,0x80001
800002a4:	af450513          	addi	a0,a0,-1292 # 80000af4 <_sp+0xffffdfac>
800002a8:	01010113          	addi	sp,sp,16
800002ac:	f39ff06f          	j	800001e4 <putString>

800002b0 <readRegister>:
800002b0:	800037b7          	lui	a5,0x80003
800002b4:	ac878793          	addi	a5,a5,-1336 # 80002ac8 <_sp+0xffffff80>
800002b8:	00251513          	slli	a0,a0,0x2
800002bc:	00f50533          	add	a0,a0,a5
800002c0:	00052503          	lw	a0,0(a0)
800002c4:	00008067          	ret

800002c8 <writeRegister>:
800002c8:	800037b7          	lui	a5,0x80003
800002cc:	00251513          	slli	a0,a0,0x2
800002d0:	ac878793          	addi	a5,a5,-1336 # 80002ac8 <_sp+0xffffff80>
800002d4:	00f50533          	add	a0,a0,a5
800002d8:	00b52023          	sw	a1,0(a0)
800002dc:	00008067          	ret

800002e0 <redirectTrap>:
800002e0:	ff010113          	addi	sp,sp,-16
800002e4:	00112623          	sw	ra,12(sp)
800002e8:	698000ef          	jal	ra,80000980 <stopSim>
800002ec:	343027f3          	csrr	a5,mtval
800002f0:	14379073          	csrw	stval,a5
800002f4:	341027f3          	csrr	a5,mepc
800002f8:	14179073          	csrw	sepc,a5
800002fc:	342027f3          	csrr	a5,mcause
80000300:	14279073          	csrw	scause,a5
80000304:	105027f3          	csrr	a5,stvec
80000308:	34179073          	csrw	mepc,a5
8000030c:	00c12083          	lw	ra,12(sp)
80000310:	01010113          	addi	sp,sp,16
80000314:	00008067          	ret

80000318 <emulationTrapToSupervisorTrap>:
80000318:	800007b7          	lui	a5,0x80000
8000031c:	00078793          	mv	a5,a5
80000320:	30579073          	csrw	mtvec,a5
80000324:	343027f3          	csrr	a5,mtval
80000328:	14379073          	csrw	stval,a5
8000032c:	342027f3          	csrr	a5,mcause
80000330:	14279073          	csrw	scause,a5
80000334:	14151073          	csrw	sepc,a0
80000338:	105027f3          	csrr	a5,stvec
8000033c:	34179073          	csrw	mepc,a5
80000340:	0035d793          	srli	a5,a1,0x3
80000344:	00459713          	slli	a4,a1,0x4
80000348:	02077713          	andi	a4,a4,32
8000034c:	1007f793          	andi	a5,a5,256
80000350:	00e7e7b3          	or	a5,a5,a4
80000354:	ffffe737          	lui	a4,0xffffe
80000358:	6dd70713          	addi	a4,a4,1757 # ffffe6dd <_sp+0x7fffbb95>
8000035c:	00e5f5b3          	and	a1,a1,a4
80000360:	00b7e7b3          	or	a5,a5,a1
80000364:	000015b7          	lui	a1,0x1
80000368:	88058593          	addi	a1,a1,-1920 # 880 <__stack_size-0x1780>
8000036c:	00b7e7b3          	or	a5,a5,a1
80000370:	30079073          	csrw	mstatus,a5
80000374:	00008067          	ret

80000378 <readWord>:
80000378:	00020737          	lui	a4,0x20
8000037c:	30072073          	csrs	mstatus,a4
80000380:	00000717          	auipc	a4,0x0
80000384:	01870713          	addi	a4,a4,24 # 80000398 <readWord+0x20>
80000388:	30571073          	csrw	mtvec,a4
8000038c:	00100693          	li	a3,1
80000390:	00052783          	lw	a5,0(a0)
80000394:	00000693          	li	a3,0
80000398:	00020737          	lui	a4,0x20
8000039c:	30073073          	csrc	mstatus,a4
800003a0:	00068513          	mv	a0,a3
800003a4:	00f5a023          	sw	a5,0(a1)
800003a8:	00008067          	ret

800003ac <readWordUnaligned>:
800003ac:	00020737          	lui	a4,0x20
800003b0:	30072073          	csrs	mstatus,a4
800003b4:	00000717          	auipc	a4,0x0
800003b8:	03c70713          	addi	a4,a4,60 # 800003f0 <readWordUnaligned+0x44>
800003bc:	30571073          	csrw	mtvec,a4
800003c0:	00100693          	li	a3,1
800003c4:	00054783          	lbu	a5,0(a0)
800003c8:	00154703          	lbu	a4,1(a0)
800003cc:	00871713          	slli	a4,a4,0x8
800003d0:	00e7e7b3          	or	a5,a5,a4
800003d4:	00254703          	lbu	a4,2(a0)
800003d8:	01071713          	slli	a4,a4,0x10
800003dc:	00e7e7b3          	or	a5,a5,a4
800003e0:	00354703          	lbu	a4,3(a0)
800003e4:	01871713          	slli	a4,a4,0x18
800003e8:	00e7e7b3          	or	a5,a5,a4
800003ec:	00000693          	li	a3,0
800003f0:	00020737          	lui	a4,0x20
800003f4:	30073073          	csrc	mstatus,a4
800003f8:	00068513          	mv	a0,a3
800003fc:	00f5a023          	sw	a5,0(a1)
80000400:	00008067          	ret

80000404 <readHalfUnaligned>:
80000404:	00020737          	lui	a4,0x20
80000408:	30072073          	csrs	mstatus,a4
8000040c:	00000717          	auipc	a4,0x0
80000410:	02470713          	addi	a4,a4,36 # 80000430 <readHalfUnaligned+0x2c>
80000414:	30571073          	csrw	mtvec,a4
80000418:	00100693          	li	a3,1
8000041c:	00150783          	lb	a5,1(a0)
80000420:	00879793          	slli	a5,a5,0x8
80000424:	00054703          	lbu	a4,0(a0)
80000428:	00e7e7b3          	or	a5,a5,a4
8000042c:	00000693          	li	a3,0
80000430:	00020737          	lui	a4,0x20
80000434:	30073073          	csrc	mstatus,a4
80000438:	00068513          	mv	a0,a3
8000043c:	00f5a023          	sw	a5,0(a1)
80000440:	00008067          	ret

80000444 <writeWord>:
80000444:	00020737          	lui	a4,0x20
80000448:	30072073          	csrs	mstatus,a4
8000044c:	00000717          	auipc	a4,0x0
80000450:	01870713          	addi	a4,a4,24 # 80000464 <writeWord+0x20>
80000454:	30571073          	csrw	mtvec,a4
80000458:	00100793          	li	a5,1
8000045c:	00b52023          	sw	a1,0(a0)
80000460:	00000793          	li	a5,0
80000464:	00020737          	lui	a4,0x20
80000468:	30073073          	csrc	mstatus,a4
8000046c:	00078513          	mv	a0,a5
80000470:	00008067          	ret

80000474 <writeWordUnaligned>:
80000474:	00020737          	lui	a4,0x20
80000478:	30072073          	csrs	mstatus,a4
8000047c:	00000717          	auipc	a4,0x0
80000480:	03070713          	addi	a4,a4,48 # 800004ac <writeWordUnaligned+0x38>
80000484:	30571073          	csrw	mtvec,a4
80000488:	00100793          	li	a5,1
8000048c:	00b50023          	sb	a1,0(a0)
80000490:	0085d593          	srli	a1,a1,0x8
80000494:	00b500a3          	sb	a1,1(a0)
80000498:	0085d593          	srli	a1,a1,0x8
8000049c:	00b50123          	sb	a1,2(a0)
800004a0:	0085d593          	srli	a1,a1,0x8
800004a4:	00b501a3          	sb	a1,3(a0)
800004a8:	00000793          	li	a5,0
800004ac:	00020737          	lui	a4,0x20
800004b0:	30073073          	csrc	mstatus,a4
800004b4:	00078513          	mv	a0,a5
800004b8:	00008067          	ret

800004bc <writeShortUnaligned>:
800004bc:	00020737          	lui	a4,0x20
800004c0:	30072073          	csrs	mstatus,a4
800004c4:	00000717          	auipc	a4,0x0
800004c8:	02070713          	addi	a4,a4,32 # 800004e4 <writeShortUnaligned+0x28>
800004cc:	30571073          	csrw	mtvec,a4
800004d0:	00100793          	li	a5,1
800004d4:	00b50023          	sb	a1,0(a0)
800004d8:	0085d593          	srli	a1,a1,0x8
800004dc:	00b500a3          	sb	a1,1(a0)
800004e0:	00000793          	li	a5,0
800004e4:	00020737          	lui	a4,0x20
800004e8:	30073073          	csrc	mstatus,a4
800004ec:	00078513          	mv	a0,a5
800004f0:	00008067          	ret

800004f4 <readInstruction>:
800004f4:	fe010113          	addi	sp,sp,-32
800004f8:	00112e23          	sw	ra,28(sp)
800004fc:	00812c23          	sw	s0,24(sp)
80000500:	00257793          	andi	a5,a0,2
80000504:	06078263          	beqz	a5,80000568 <readInstruction+0x74>
80000508:	00050413          	mv	s0,a0
8000050c:	00810593          	addi	a1,sp,8
80000510:	ffe50513          	addi	a0,a0,-2
80000514:	e65ff0ef          	jal	ra,80000378 <readWord>
80000518:	00a15783          	lhu	a5,10(sp)
8000051c:	00300713          	li	a4,3
80000520:	00f12423          	sw	a5,8(sp)
80000524:	0037f793          	andi	a5,a5,3
80000528:	02e79263          	bne	a5,a4,8000054c <readInstruction+0x58>
8000052c:	00c10593          	addi	a1,sp,12
80000530:	00240513          	addi	a0,s0,2
80000534:	e45ff0ef          	jal	ra,80000378 <readWord>
80000538:	00c12783          	lw	a5,12(sp)
8000053c:	01079713          	slli	a4,a5,0x10
80000540:	00812783          	lw	a5,8(sp)
80000544:	00e7e7b3          	or	a5,a5,a4
80000548:	00f12423          	sw	a5,8(sp)
8000054c:	00812783          	lw	a5,8(sp)
80000550:	00f12623          	sw	a5,12(sp)
80000554:	01c12083          	lw	ra,28(sp)
80000558:	01812403          	lw	s0,24(sp)
8000055c:	00c12503          	lw	a0,12(sp)
80000560:	02010113          	addi	sp,sp,32
80000564:	00008067          	ret
80000568:	00c10593          	addi	a1,sp,12
8000056c:	e0dff0ef          	jal	ra,80000378 <readWord>
80000570:	fe5ff06f          	j	80000554 <readInstruction+0x60>

80000574 <trap>:
80000574:	fd010113          	addi	sp,sp,-48
80000578:	02112623          	sw	ra,44(sp)
8000057c:	02812423          	sw	s0,40(sp)
80000580:	02912223          	sw	s1,36(sp)
80000584:	03212023          	sw	s2,32(sp)
80000588:	01312e23          	sw	s3,28(sp)
8000058c:	01412c23          	sw	s4,24(sp)
80000590:	01512a23          	sw	s5,20(sp)
80000594:	34202473          	csrr	s0,mcause
80000598:	04045263          	bgez	s0,800005dc <trap+0x68>
8000059c:	0ff47413          	andi	s0,s0,255
800005a0:	00700793          	li	a5,7
800005a4:	1ef41263          	bne	s0,a5,80000788 <trap+0x214>
800005a8:	02000793          	li	a5,32
800005ac:	1447a073          	csrs	sip,a5
800005b0:	08000793          	li	a5,128
800005b4:	3047b073          	csrc	mie,a5
800005b8:	02c12083          	lw	ra,44(sp)
800005bc:	02812403          	lw	s0,40(sp)
800005c0:	02412483          	lw	s1,36(sp)
800005c4:	02012903          	lw	s2,32(sp)
800005c8:	01c12983          	lw	s3,28(sp)
800005cc:	01812a03          	lw	s4,24(sp)
800005d0:	01412a83          	lw	s5,20(sp)
800005d4:	03010113          	addi	sp,sp,48
800005d8:	00008067          	ret
800005dc:	00400793          	li	a5,4
800005e0:	08f40263          	beq	s0,a5,80000664 <trap+0xf0>
800005e4:	0287e863          	bltu	a5,s0,80000614 <trap+0xa0>
800005e8:	00200793          	li	a5,2
800005ec:	16f40c63          	beq	s0,a5,80000764 <trap+0x1f0>
800005f0:	80001537          	lui	a0,0x80001
800005f4:	b1850513          	addi	a0,a0,-1256 # 80000b18 <_sp+0xffffdfd0>
800005f8:	bedff0ef          	jal	ra,800001e4 <putString>
800005fc:	00040513          	mv	a0,s0
80000600:	b7dff0ef          	jal	ra,8000017c <putHex>
80000604:	80001537          	lui	a0,0x80001
80000608:	b1450513          	addi	a0,a0,-1260 # 80000b14 <_sp+0xffffdfcc>
8000060c:	bd9ff0ef          	jal	ra,800001e4 <putString>
80000610:	1780006f          	j	80000788 <trap+0x214>
80000614:	00600793          	li	a5,6
80000618:	0ef40063          	beq	s0,a5,800006f8 <trap+0x184>
8000061c:	00900793          	li	a5,9
80000620:	fcf418e3          	bne	s0,a5,800005f0 <trap+0x7c>
80000624:	80003437          	lui	s0,0x80003
80000628:	b4840413          	addi	s0,s0,-1208 # 80002b48 <_sp+0x0>
8000062c:	fc442783          	lw	a5,-60(s0)
80000630:	00100713          	li	a4,1
80000634:	fa842503          	lw	a0,-88(s0)
80000638:	30e78863          	beq	a5,a4,80000948 <trap+0x3d4>
8000063c:	00200713          	li	a4,2
80000640:	32e78063          	beq	a5,a4,80000960 <trap+0x3ec>
80000644:	32079463          	bnez	a5,8000096c <trap+0x3f8>
80000648:	fac42583          	lw	a1,-84(s0)
8000064c:	3cc000ef          	jal	ra,80000a18 <setMachineTimerCmp>
80000650:	08000793          	li	a5,128
80000654:	3047a073          	csrs	mie,a5
80000658:	02000793          	li	a5,32
8000065c:	1447b073          	csrc	sip,a5
80000660:	2f00006f          	j	80000950 <trap+0x3dc>
80000664:	34102473          	csrr	s0,mepc
80000668:	30002973          	csrr	s2,mstatus
8000066c:	00040513          	mv	a0,s0
80000670:	e85ff0ef          	jal	ra,800004f4 <readInstruction>
80000674:	00050493          	mv	s1,a0
80000678:	34302573          	csrr	a0,mtval
8000067c:	00c4d793          	srli	a5,s1,0xc
80000680:	0077f793          	andi	a5,a5,7
80000684:	00200713          	li	a4,2
80000688:	04e78863          	beq	a5,a4,800006d8 <trap+0x164>
8000068c:	00500713          	li	a4,5
80000690:	04e78a63          	beq	a5,a4,800006e4 <trap+0x170>
80000694:	00100713          	li	a4,1
80000698:	00e79863          	bne	a5,a4,800006a8 <trap+0x134>
8000069c:	00c10593          	addi	a1,sp,12
800006a0:	d65ff0ef          	jal	ra,80000404 <readHalfUnaligned>
800006a4:	00050993          	mv	s3,a0
800006a8:	00090593          	mv	a1,s2
800006ac:	08099863          	bnez	s3,8000073c <trap+0x1c8>
800006b0:	00c12583          	lw	a1,12(sp)
800006b4:	0074d513          	srli	a0,s1,0x7
800006b8:	01f57513          	andi	a0,a0,31
800006bc:	c0dff0ef          	jal	ra,800002c8 <writeRegister>
800006c0:	00440413          	addi	s0,s0,4
800006c4:	34141073          	csrw	mepc,s0
800006c8:	800007b7          	lui	a5,0x80000
800006cc:	00078793          	mv	a5,a5
800006d0:	30579073          	csrw	mtvec,a5
800006d4:	ee5ff06f          	j	800005b8 <trap+0x44>
800006d8:	00c10593          	addi	a1,sp,12
800006dc:	cd1ff0ef          	jal	ra,800003ac <readWordUnaligned>
800006e0:	fc5ff06f          	j	800006a4 <trap+0x130>
800006e4:	00c10593          	addi	a1,sp,12
800006e8:	d1dff0ef          	jal	ra,80000404 <readHalfUnaligned>
800006ec:	01051993          	slli	s3,a0,0x10
800006f0:	0109d993          	srli	s3,s3,0x10
800006f4:	fb5ff06f          	j	800006a8 <trap+0x134>
800006f8:	34102473          	csrr	s0,mepc
800006fc:	300029f3          	csrr	s3,mstatus
80000700:	00040513          	mv	a0,s0
80000704:	df1ff0ef          	jal	ra,800004f4 <readInstruction>
80000708:	34302973          	csrr	s2,mtval
8000070c:	00c55493          	srli	s1,a0,0xc
80000710:	01455513          	srli	a0,a0,0x14
80000714:	01f57513          	andi	a0,a0,31
80000718:	b99ff0ef          	jal	ra,800002b0 <readRegister>
8000071c:	0074f493          	andi	s1,s1,7
80000720:	00100793          	li	a5,1
80000724:	00050593          	mv	a1,a0
80000728:	02f48063          	beq	s1,a5,80000748 <trap+0x1d4>
8000072c:	00200793          	li	a5,2
80000730:	02f48463          	beq	s1,a5,80000758 <trap+0x1e4>
80000734:	f80a06e3          	beqz	s4,800006c0 <trap+0x14c>
80000738:	00098593          	mv	a1,s3
8000073c:	00040513          	mv	a0,s0
80000740:	bd9ff0ef          	jal	ra,80000318 <emulationTrapToSupervisorTrap>
80000744:	e75ff06f          	j	800005b8 <trap+0x44>
80000748:	00090513          	mv	a0,s2
8000074c:	d71ff0ef          	jal	ra,800004bc <writeShortUnaligned>
80000750:	00050a13          	mv	s4,a0
80000754:	fe1ff06f          	j	80000734 <trap+0x1c0>
80000758:	00090513          	mv	a0,s2
8000075c:	d19ff0ef          	jal	ra,80000474 <writeWordUnaligned>
80000760:	ff1ff06f          	j	80000750 <trap+0x1dc>
80000764:	341029f3          	csrr	s3,mepc
80000768:	30002a73          	csrr	s4,mstatus
8000076c:	343024f3          	csrr	s1,mtval
80000770:	02f00693          	li	a3,47
80000774:	07f4f713          	andi	a4,s1,127
80000778:	00c4d793          	srli	a5,s1,0xc
8000077c:	00d70a63          	beq	a4,a3,80000790 <trap+0x21c>
80000780:	07300693          	li	a3,115
80000784:	12d70663          	beq	a4,a3,800008b0 <trap+0x33c>
80000788:	b59ff0ef          	jal	ra,800002e0 <redirectTrap>
8000078c:	e2dff06f          	j	800005b8 <trap+0x44>
80000790:	0077f793          	andi	a5,a5,7
80000794:	fe879ae3          	bne	a5,s0,80000788 <trap+0x214>
80000798:	00f4d513          	srli	a0,s1,0xf
8000079c:	01f57513          	andi	a0,a0,31
800007a0:	b11ff0ef          	jal	ra,800002b0 <readRegister>
800007a4:	00050a93          	mv	s5,a0
800007a8:	0144d513          	srli	a0,s1,0x14
800007ac:	01f57513          	andi	a0,a0,31
800007b0:	b01ff0ef          	jal	ra,800002b0 <readRegister>
800007b4:	00050413          	mv	s0,a0
800007b8:	00c10593          	addi	a1,sp,12
800007bc:	000a8513          	mv	a0,s5
800007c0:	01b4d913          	srli	s2,s1,0x1b
800007c4:	bb5ff0ef          	jal	ra,80000378 <readWord>
800007c8:	00050863          	beqz	a0,800007d8 <trap+0x264>
800007cc:	000a0593          	mv	a1,s4
800007d0:	00098513          	mv	a0,s3
800007d4:	f6dff06f          	j	80000740 <trap+0x1cc>
800007d8:	00c00793          	li	a5,12
800007dc:	0af90863          	beq	s2,a5,8000088c <trap+0x318>
800007e0:	0327e663          	bltu	a5,s2,8000080c <trap+0x298>
800007e4:	00100793          	li	a5,1
800007e8:	06f90663          	beq	s2,a5,80000854 <trap+0x2e0>
800007ec:	06090063          	beqz	s2,8000084c <trap+0x2d8>
800007f0:	00400793          	li	a5,4
800007f4:	08f90663          	beq	s2,a5,80000880 <trap+0x30c>
800007f8:	00800793          	li	a5,8
800007fc:	f8f916e3          	bne	s2,a5,80000788 <trap+0x214>
80000800:	00c12783          	lw	a5,12(sp)
80000804:	00f46433          	or	s0,s0,a5
80000808:	04c0006f          	j	80000854 <trap+0x2e0>
8000080c:	01400793          	li	a5,20
80000810:	08f90463          	beq	s2,a5,80000898 <trap+0x324>
80000814:	0127ee63          	bltu	a5,s2,80000830 <trap+0x2bc>
80000818:	01000793          	li	a5,16
8000081c:	f6f916e3          	bne	s2,a5,80000788 <trap+0x214>
80000820:	00c12783          	lw	a5,12(sp)
80000824:	0287d863          	bge	a5,s0,80000854 <trap+0x2e0>
80000828:	00078413          	mv	s0,a5
8000082c:	0280006f          	j	80000854 <trap+0x2e0>
80000830:	01800793          	li	a5,24
80000834:	06f90863          	beq	s2,a5,800008a4 <trap+0x330>
80000838:	01c00793          	li	a5,28
8000083c:	f4f916e3          	bne	s2,a5,80000788 <trap+0x214>
80000840:	00c12783          	lw	a5,12(sp)
80000844:	00f47863          	bgeu	s0,a5,80000854 <trap+0x2e0>
80000848:	fe1ff06f          	j	80000828 <trap+0x2b4>
8000084c:	00c12783          	lw	a5,12(sp)
80000850:	00f40433          	add	s0,s0,a5
80000854:	00040593          	mv	a1,s0
80000858:	000a8513          	mv	a0,s5
8000085c:	be9ff0ef          	jal	ra,80000444 <writeWord>
80000860:	f60516e3          	bnez	a0,800007cc <trap+0x258>
80000864:	00c12583          	lw	a1,12(sp)
80000868:	0074d513          	srli	a0,s1,0x7
8000086c:	01f57513          	andi	a0,a0,31
80000870:	a59ff0ef          	jal	ra,800002c8 <writeRegister>
80000874:	00498993          	addi	s3,s3,4
80000878:	34199073          	csrw	mepc,s3
8000087c:	e4dff06f          	j	800006c8 <trap+0x154>
80000880:	00c12783          	lw	a5,12(sp)
80000884:	00f44433          	xor	s0,s0,a5
80000888:	fcdff06f          	j	80000854 <trap+0x2e0>
8000088c:	00c12783          	lw	a5,12(sp)
80000890:	00f47433          	and	s0,s0,a5
80000894:	fc1ff06f          	j	80000854 <trap+0x2e0>
80000898:	00c12783          	lw	a5,12(sp)
8000089c:	faf45ce3          	bge	s0,a5,80000854 <trap+0x2e0>
800008a0:	f89ff06f          	j	80000828 <trap+0x2b4>
800008a4:	00c12783          	lw	a5,12(sp)
800008a8:	fa87f6e3          	bgeu	a5,s0,80000854 <trap+0x2e0>
800008ac:	f7dff06f          	j	80000828 <trap+0x2b4>
800008b0:	0037f793          	andi	a5,a5,3
800008b4:	04878863          	beq	a5,s0,80000904 <trap+0x390>
800008b8:	00300713          	li	a4,3
800008bc:	04e78463          	beq	a5,a4,80000904 <trap+0x390>
800008c0:	00100713          	li	a4,1
800008c4:	00100413          	li	s0,1
800008c8:	00e78463          	beq	a5,a4,800008d0 <trap+0x35c>
800008cc:	a15ff0ef          	jal	ra,800002e0 <redirectTrap>
800008d0:	00001737          	lui	a4,0x1
800008d4:	0144d793          	srli	a5,s1,0x14
800008d8:	c0070693          	addi	a3,a4,-1024 # c00 <__stack_size-0x1400>
800008dc:	02d7e063          	bltu	a5,a3,800008fc <trap+0x388>
800008e0:	c0270713          	addi	a4,a4,-1022
800008e4:	02f77863          	bgeu	a4,a5,80000914 <trap+0x3a0>
800008e8:	fffff737          	lui	a4,0xfffff
800008ec:	38070713          	addi	a4,a4,896 # fffff380 <_sp+0x7fffc838>
800008f0:	00e787b3          	add	a5,a5,a4
800008f4:	00200713          	li	a4,2
800008f8:	04f77463          	bgeu	a4,a5,80000940 <trap+0x3cc>
800008fc:	9e5ff0ef          	jal	ra,800002e0 <redirectTrap>
80000900:	01c0006f          	j	8000091c <trap+0x3a8>
80000904:	00f4d413          	srli	s0,s1,0xf
80000908:	01f47413          	andi	s0,s0,31
8000090c:	00803433          	snez	s0,s0
80000910:	fc1ff06f          	j	800008d0 <trap+0x35c>
80000914:	0ec000ef          	jal	ra,80000a00 <rdtime>
80000918:	00050913          	mv	s2,a0
8000091c:	00040463          	beqz	s0,80000924 <trap+0x3b0>
80000920:	9c1ff0ef          	jal	ra,800002e0 <redirectTrap>
80000924:	0074d493          	srli	s1,s1,0x7
80000928:	00090593          	mv	a1,s2
8000092c:	01f4f513          	andi	a0,s1,31
80000930:	999ff0ef          	jal	ra,800002c8 <writeRegister>
80000934:	00498993          	addi	s3,s3,4
80000938:	34199073          	csrw	mepc,s3
8000093c:	c7dff06f          	j	800005b8 <trap+0x44>
80000940:	0cc000ef          	jal	ra,80000a0c <rdtimeh>
80000944:	fd5ff06f          	j	80000918 <trap+0x3a4>
80000948:	0ff57513          	andi	a0,a0,255
8000094c:	068000ef          	jal	ra,800009b4 <putC>
80000950:	341027f3          	csrr	a5,mepc
80000954:	00478793          	addi	a5,a5,4 # 80000004 <_sp+0xffffd4bc>
80000958:	34179073          	csrw	mepc,a5
8000095c:	c5dff06f          	j	800005b8 <trap+0x44>
80000960:	070000ef          	jal	ra,800009d0 <getC>
80000964:	faa42423          	sw	a0,-88(s0)
80000968:	fe9ff06f          	j	80000950 <trap+0x3dc>
8000096c:	80001537          	lui	a0,0x80001
80000970:	b0850513          	addi	a0,a0,-1272 # 80000b08 <_sp+0xffffdfc0>
80000974:	871ff0ef          	jal	ra,800001e4 <putString>
80000978:	008000ef          	jal	ra,80000980 <stopSim>
8000097c:	c3dff06f          	j	800005b8 <trap+0x44>

80000980 <stopSim>:
80000980:	800017b7          	lui	a5,0x80001
80000984:	b2c78793          	addi	a5,a5,-1236 # 80000b2c <_sp+0xffffdfe4>
80000988:	10010637          	lui	a2,0x10010
8000098c:	0007c683          	lbu	a3,0(a5)
80000990:	00069463          	bnez	a3,80000998 <stopSim+0x18>
80000994:	0000006f          	j	80000994 <stopSim+0x14>
80000998:	00178793          	addi	a5,a5,1
8000099c:	00462703          	lw	a4,4(a2) # 10010004 <__stack_size+0x1000e004>
800009a0:	01075713          	srli	a4,a4,0x10
800009a4:	0ff77713          	andi	a4,a4,255
800009a8:	fe070ae3          	beqz	a4,8000099c <stopSim+0x1c>
800009ac:	00d62023          	sw	a3,0(a2)
800009b0:	fddff06f          	j	8000098c <stopSim+0xc>

800009b4 <putC>:
800009b4:	10010737          	lui	a4,0x10010
800009b8:	00472783          	lw	a5,4(a4) # 10010004 <__stack_size+0x1000e004>
800009bc:	0107d793          	srli	a5,a5,0x10
800009c0:	0ff7f793          	andi	a5,a5,255
800009c4:	fe078ae3          	beqz	a5,800009b8 <putC+0x4>
800009c8:	00a72023          	sw	a0,0(a4)
800009cc:	00008067          	ret

800009d0 <getC>:
800009d0:	100107b7          	lui	a5,0x10010
800009d4:	0047a783          	lw	a5,4(a5) # 10010004 <__stack_size+0x1000e004>
800009d8:	fff00513          	li	a0,-1
800009dc:	0187d793          	srli	a5,a5,0x18
800009e0:	00078e63          	beqz	a5,800009fc <getC+0x2c>
800009e4:	10010737          	lui	a4,0x10010
800009e8:	00472783          	lw	a5,4(a4) # 10010004 <__stack_size+0x1000e004>
800009ec:	0187d793          	srli	a5,a5,0x18
800009f0:	fe078ce3          	beqz	a5,800009e8 <getC+0x18>
800009f4:	00072503          	lw	a0,0(a4)
800009f8:	0ff57513          	andi	a0,a0,255
800009fc:	00008067          	ret

80000a00 <rdtime>:
80000a00:	100087b7          	lui	a5,0x10008
80000a04:	0007a503          	lw	a0,0(a5) # 10008000 <__stack_size+0x10006000>
80000a08:	00008067          	ret

80000a0c <rdtimeh>:
80000a0c:	100087b7          	lui	a5,0x10008
80000a10:	0047a503          	lw	a0,4(a5) # 10008004 <__stack_size+0x10006004>
80000a14:	00008067          	ret

80000a18 <setMachineTimerCmp>:
80000a18:	100087b7          	lui	a5,0x10008
80000a1c:	fff00713          	li	a4,-1
80000a20:	00e7a623          	sw	a4,12(a5) # 1000800c <__stack_size+0x1000600c>
80000a24:	00a7a423          	sw	a0,8(a5)
80000a28:	00b7a623          	sw	a1,12(a5)
80000a2c:	00008067          	ret

80000a30 <halInit>:
80000a30:	00008067          	ret

80000a34 <__libc_init_array>:
80000a34:	ff010113          	addi	sp,sp,-16
80000a38:	800017b7          	lui	a5,0x80001
80000a3c:	00812423          	sw	s0,8(sp)
80000a40:	80001437          	lui	s0,0x80001
80000a44:	adc78713          	addi	a4,a5,-1316 # 80000adc <_sp+0xffffdf94>
80000a48:	adc40413          	addi	s0,s0,-1316 # 80000adc <_sp+0xffffdf94>
80000a4c:	40e40433          	sub	s0,s0,a4
80000a50:	00912223          	sw	s1,4(sp)
80000a54:	01212023          	sw	s2,0(sp)
80000a58:	00112623          	sw	ra,12(sp)
80000a5c:	40245413          	srai	s0,s0,0x2
80000a60:	00000493          	li	s1,0
80000a64:	adc78913          	addi	s2,a5,-1316
80000a68:	04849263          	bne	s1,s0,80000aac <__libc_init_array+0x78>
80000a6c:	f0cff0ef          	jal	ra,80000178 <_init>
80000a70:	800017b7          	lui	a5,0x80001
80000a74:	80001437          	lui	s0,0x80001
80000a78:	adc78713          	addi	a4,a5,-1316 # 80000adc <_sp+0xffffdf94>
80000a7c:	adc40413          	addi	s0,s0,-1316 # 80000adc <_sp+0xffffdf94>
80000a80:	40e40433          	sub	s0,s0,a4
80000a84:	40245413          	srai	s0,s0,0x2
80000a88:	00000493          	li	s1,0
80000a8c:	adc78913          	addi	s2,a5,-1316
80000a90:	02849a63          	bne	s1,s0,80000ac4 <__libc_init_array+0x90>
80000a94:	00c12083          	lw	ra,12(sp)
80000a98:	00812403          	lw	s0,8(sp)
80000a9c:	00412483          	lw	s1,4(sp)
80000aa0:	00012903          	lw	s2,0(sp)
80000aa4:	01010113          	addi	sp,sp,16
80000aa8:	00008067          	ret
80000aac:	00249793          	slli	a5,s1,0x2
80000ab0:	00f907b3          	add	a5,s2,a5
80000ab4:	0007a783          	lw	a5,0(a5)
80000ab8:	00148493          	addi	s1,s1,1
80000abc:	000780e7          	jalr	a5
80000ac0:	fa9ff06f          	j	80000a68 <__libc_init_array+0x34>
80000ac4:	00249793          	slli	a5,s1,0x2
80000ac8:	00f907b3          	add	a5,s2,a5
80000acc:	0007a783          	lw	a5,0(a5)
80000ad0:	00148493          	addi	s1,s1,1
80000ad4:	000780e7          	jalr	a5
80000ad8:	fb9ff06f          	j	80000a90 <__libc_init_array+0x5c>
